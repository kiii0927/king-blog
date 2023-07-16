package com.king.common.utils;

import com.google.common.base.Strings;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 * <p>
 *    高频方法集合类
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-06-21
 **/
public final class ToolUtil {

    private ToolUtil(){
        throw new AssertionError("No com.king.common.utils.ToolUtil instances for you!");
    }

    /**
     * 获取异常的具体信息
     *
     * @param e exception
     * @return string
     */
    public static String getExceptionMsg(Exception e) {
        StringWriter sw = new StringWriter();
        try {
            e.printStackTrace(new PrintWriter(sw));
        } finally {
            try {
                sw.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return sw.getBuffer().toString().replaceAll("\\$", "T");
    }


    /**
     * 判断是否是windows操作系统
     */
    public static Boolean isWinOs() {
        String os = System.getProperty("os.name");
        return os.toLowerCase().startsWith("win");
    }

    /**
     * 获取项目路径
     */
    public static String getWebRootPath(String filePath) {
        try {
            String path = Objects.requireNonNull(ToolUtil.class.getClassLoader().getResource("")).toURI().getPath();
            path = path.replace("/WEB-INF/classes/", "");
            path = path.replace("/target/classes/", "");
            path = path.replace("file:/", "");
            if (Strings.isNullOrEmpty(filePath)) {
                return path;
            } else {
                return path + "/" + filePath;
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 转换为base64字符串
     * @param target 对象值
     * @return base64字符串
     */
    public static String toBase64String(String target) {
        Objects.requireNonNull(target);
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] bytes = target.getBytes(StandardCharsets.UTF_8);
        return encoder.encodeToString(bytes);
    }
}
