package com.king.common.exception;

/**
 * <p>
 *   服务层异常
 * </p>
 * @author: k
 * @create: 2022-11-14 15:35
 **/
public class ServiceException extends BaseException {


    public ServiceException(){
        super("Error");
    }

    public ServiceException(String message){
        super(message);
    }

    public ServiceException(Integer code, String message){
        super(ServiceException.class.getName() ,code,message);
    }
}
