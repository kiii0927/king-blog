package com.king.common.exception;

import com.king.common.module.domain.ResponseCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <P>
 *    自定义异常基础类
 * </P>
 *
 * @author: k
 * @create: 2022-11-14 15:26
 **/
@Getter
@Setter
public class BaseException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 所属模块
     */
    private String module;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String defaultMessage = "操作失败！";


    public BaseException() {
        this("操作失败");
    }


    public BaseException(String message) {
        this(BaseException.class.getName(), ResponseCode.ERROR.getCode(), message);
    }

    public BaseException(Integer code, String message) {
        this(BaseException.class.getName(), code, message);
    }

    public BaseException(String module, Integer code, String message) {
        this.module = module;
        this.code = code;
        this.defaultMessage = message;
    }
}
