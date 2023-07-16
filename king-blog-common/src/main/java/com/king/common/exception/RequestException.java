package com.king.common.exception;

/**
 * <p>
 *    请求异常
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-16
 **/
public class RequestException extends BaseException {

    public RequestException(){
        super("请求失败");
    }

    public RequestException(String message){
        super(message);
    }

    public RequestException(Integer code, String message){
        super(ServiceException.class.getName() ,code,message);
    }

}
