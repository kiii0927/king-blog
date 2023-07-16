package com.king.sys.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.sys.bean.dto.VerifyCodeDto;
import com.king.sys.bean.entity.system.Validation;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * <p>
 *    服务类
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-03
 **/
public interface IValidationService extends IService<Validation> {

    /**
     * 保存邮箱验证码
     *
     * @param email 接收者邮箱
     * @param code 验证码
     * @param type 验证类型
     * @param offsetMinute 发送时间
     */
    void saveCode(String email, String code, Integer type, Date offsetMinute);

    void saveCode(Validation validation);

    /**
     * 校验邮箱是否存在
     * @param email 校验的邮箱
     * @return true/false
     */
    boolean validationEmailIsExist(String email);

    /**
     * 校验用户是否存在
     * @param username 校验的用户名
     * @return true/false
     */
    boolean validationUserIsExist(String username);

    /**
     * 校验邮箱验证码是否合法
     * @param validationCodeDto {@link VerifyCodeDto}
     * @return true/false
     */
    boolean validationEmailCode(VerifyCodeDto validationCodeDto);

    /**
     * 校验邮箱验证码
     * @param email 邮箱
     * @param now 当前时间
     * @return true/false
     */
    boolean validationEmailCode(String email, Date now);

    /**
     * 校验图片验证码
     * @param vcd {@link VerifyCodeDto}
     */
    void validationImgCode(VerifyCodeDto vcd, HttpServletRequest request);

}
