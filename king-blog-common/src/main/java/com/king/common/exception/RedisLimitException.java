package com.king.common.exception;


import com.king.common.module.domain.ResponseCode;

/**
 * Redis限流自定义异常
 * @author king
 * @since 2023-03-12
 **/
public class RedisLimitException extends BaseException {

    public RedisLimitException(){
        this("系统繁忙,请稍后再试.");
    }

    public RedisLimitException(String message){
        super(ResponseCode.REQUEST_EXCEPTION.getCode(), message);
    }

}
