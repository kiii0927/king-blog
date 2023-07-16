package com.king.common.utils;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import com.google.common.base.Strings;
import com.king.common.exception.WrongInputException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author king
 * @version 1.0
 * @since 2023-06-21
 **/
@Slf4j
public final class CodeUtil {

    private CodeUtil(){
        throw new AssertionError("No com.king.common.utils.CodeUtil instances for you!");
    }

    /**
     * 利用 hutool 工具，生成验证码图片资源
     */
    private final static ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(90, 30, 4, 4);

    /**
     * 过期时间(单位/分)
     */
    private final static long EXPIRE_TIME = 5;

    /**
     * 利用Redis存放code
     */
    private final static StringRedisTemplate redisTemplate = new StringRedisTemplate();

    ///**
    // * 存放 code
    // */
    //private final static Map<String, String> CODE_MAP = new ConcurrentHashMap<>();

    /**
     * 创建验证码
     * @param response {@link HttpServletResponse}
     */
    public static void createImgCode(HttpServletResponse response) throws IOException {

        CommonPortion();

        // 将验证码图片的二进制数据写入【响应体 response 】
        ServletOutputStream sos = response.getOutputStream();
        String imageBase64Data = captcha.getImageBase64Data(); // 获取图片带文件格式的 Base64
        sos.write(imageBase64Data.getBytes(StandardCharsets.UTF_8));

        // 关闭流
        sos.close();
    }

    /**
     * 创建图片验证码<br />
     *
     * @return map <code>
     *     new HashMap<String, String>(){
     *         private static final long serialVersionUID = 1L;
     *         {
     *             put("code", code);
     *             put("imgCode", imageBase64Data);
     *         }
     *      };
     * </code>
     */
    public static Map<String, String> createImgCode() {
        Tuple2<String, String> tuple2 = CommonPortion();

        String imageBase64Data = captcha.getImageBase64Data(); // 获取图片带文件格式的 Base64

        // 创建 map 并返回
        return new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;
            {
                put("code", tuple2.getT2());
                put("image", imageBase64Data);
            }
        };
    }

    /**
     * 校验验证码
     * @return true/false
     */
    public static boolean verifyCode(@NonNull String identify , @NonNull String code){
        //String s = CODE_MAP.get(identify);
        String s = redisTemplate.opsForValue().get(identify);
        boolean verify = code.equalsIgnoreCase(s);
        if (verify) {
            //CODE_MAP.remove(identify);
            redisTemplate.delete(identify);
        }
        return verify;
    }

    /**
     * 获取用户唯一标识
     * @return 唯一标识
     */
    private static String getIdentify(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null; // 判断 attributes 是否为 null
        HttpServletRequest request = attributes.getRequest(); // 获取 request
        return request.getHeader("identify");
    }

    // 公共部分
    private static Tuple2<String, String> CommonPortion(){
        captcha.createCode();
        // 获取验证码
        String code = captcha.getCode();
        log.info("图片验证码 = {}", code);

        String identify = getIdentify();
        if (Strings.isNullOrEmpty(identify)) {
            log.warn("获取图片验证码缺少`identify`");
            throw new WrongInputException("操作失败，系统繁忙！");
        }

        // 存储
        redisTemplate.opsForValue()
                .set(identify, code, EXPIRE_TIME, TimeUnit.MINUTES);

        return Tuples.of(identify, code);
    }

}
