package com.king.common.module.domain;

/**
 * ajax result code
 *
 * @author king
 * @version 1.0
 * @since 2023-06-20
 **/
public enum ResponseCode {
    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    ERROR(400,"操作失败"),

    ///**
    // * 公共错误码
    // */
    //COMMON_FAIL(999, "操作失败"),

    /**
     * 系统异常
     */
    SYSTEM_FAIL(500,"系统异常"),

    /**
     * 参数无效
     */
    PARAM_NOT_VALID(1001, "参数无效"),

    /**
     * 参数为空
     */
    PARAM_IS_BLANK(1002, "参数为空"),

    /* 用户错误 */
    /**
     * 用户未登录
     */
    USER_NOT_LOGIN(2001, "用户未登录"),

    /**
     * 账号已过期
     */
    USER_ACCOUNT_EXPIRED(2002, "账号已过期"),

    /**
     * 密码错误错误状
     */
    USER_CREDENTIALS_ERROR(2003, "密码错误"),

    /**
     * 账号已锁定错误
     */
    USER_LOCK_ERROR(2004, "账号已锁定"),

    /**
     * 算数异常
     */
    ARITHMETIC_EXCEPTION(3001,"算数异常"),

    /**
     * 校验异常
     */
    VALIDATION_EXCEPTION(4001,"校验异常"),

    /**
     * 登录错误码
     */
    LOGIN_FAIL(5000,"登录失败"),

    /**
     * 请求异常
     */
    REQUEST_EXCEPTION(5001,"请求失败"),

    /**
     * 非法令牌
     */
    ILLEGAL_TOKEN(50012, "非法令牌"),

    /**
     * other
     */
    OTHER_FAIL(-1, "操作失败");


    /**
     * 返回状态码
     */
    private int code;

    /**
     * 返回状态信息
     */
    private String message;

    ResponseCode(int code,String message){
        this.code= code;
        this.message= message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
