package com.king.common.exception;


/**
 * 自定义异常
 * @author: k
 * @create: 2022-06-24 02:42
 **/
public class WrongInputException extends BaseException {

    public WrongInputException(){
        super();
    }

    public WrongInputException(Integer code, String message) {
        super(WrongInputException.class.getName(),code,message);
    }

    public WrongInputException(String message) {
        super(message);
    }
}
