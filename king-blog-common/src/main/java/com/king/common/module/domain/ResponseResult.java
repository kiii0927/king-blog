package com.king.common.module.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * ajax result
 *
 * @author king
 * @version 1.0
 * @since 2023-06-20
 **/
@Data
public class ResponseResult<T> implements Serializable {

    /**
     * 数据
     */
    private T data;

    /**
     * 响应编码200为成功
     */
    private Integer code;

    ///**
    // * 请求消耗时间
    // */
    //private long cost;

    /**
     * 描述
     */
    private String message;

    ///**
    // * 请求id
    // */
    //private String requestId;

    /**
     * 构造方法私有化，里面的方法都为静态方法
     * 达到保护属性的作用
     */
    private ResponseResult(){
        throw new AssertionError("No com.king.module.custom.ResponseResult instances for you!");
    }

    public ResponseResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private static <T> ResponseResult<T> create(Integer code, String message){
        return new ResponseResult<T>(code, message,null);
    }

    /**
     * 无数据返回成功
     * @return this
     */
    public static <T> ResponseResult<T> success() {
        return create(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 成功的回调
     * @param data 数据
     * @param <T> 泛型
     * @return this
     */
    public static <T> ResponseResult<T> success(T data) {
        return success("操作成功" ,data);
    }

    public static <T> ResponseResult<T> success(String message, T data){
        return success(ResponseCode.SUCCESS.getCode(),message,data);
    }

    public static <T> ResponseResult<T> success(Integer code, String message, T data) {
        return new ResponseResult<T>(code,message,data);
    }


    /**
     * 无数据返回失败
     * @return this
     */
    public static <T> ResponseResult<T> fail(){
        return create(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getMessage());
    }

    /**
     * 自定义返回失败描述
     * @param message msg
     * @return this
     */
    public static <T> ResponseResult<T> fail(String message) {
        return create(ResponseCode.ERROR.getCode(), message);
    }

    /**
     * 自定义返回失败转态码描述
     * @param code code
     * @param message msg
     * @return this
     */
    public static <T> ResponseResult<T> fail(Integer code, String message) {
        return create(code, message);
    }

}
