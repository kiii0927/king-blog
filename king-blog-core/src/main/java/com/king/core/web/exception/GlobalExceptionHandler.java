package com.king.core.web.exception;

import com.king.common.exception.RedisLimitException;
import com.king.common.exception.RequestException;
import com.king.common.exception.ServiceException;
import com.king.common.exception.WrongInputException;
import com.king.common.module.constant.Constant;
import com.king.common.module.domain.ResponseCode;
import com.king.common.module.domain.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.sql.SQLException;

/**
 * <p>
 *   全局异常处理
 * </p>
 *
 * @author: king
 * @create: 2022-11-14 09:47
 **/

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理Assert的异常
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseResult<Object> handler(IllegalArgumentException e) {
        log.error("Assert异常:------------>{}",e.getMessage());
        e.printStackTrace();
        return ResponseResult.fail(ResponseCode.ERROR.getCode(),e.getMessage());
    }

    /**
     * 自定义异常
     */
    @ExceptionHandler(WrongInputException.class)
    public ResponseResult<Object> handle(WrongInputException e){
        log.warn("自定义异常:-------------->{}",e.getDefaultMessage());
        return ResponseResult.fail(e.getCode(), e.getDefaultMessage());
    }


    /**
     * service 异常
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseResult<Object> handle(ServiceException e){
        log.error("service异常:-------------->{}",e.getDefaultMessage());
        return ResponseResult.fail(e.getCode(), e.getDefaultMessage());
    }

    /**
     * 请求异常
     */
    @ExceptionHandler(RequestException.class)
    public ResponseResult<Object> handle(RequestException e){
        log.error("请求异常:-------------->{}",e.getDefaultMessage());
        return ResponseResult.fail(e.getCode(), e.getDefaultMessage());
    }


    /**
     * redis限流异常
     */
    @ExceptionHandler(RedisLimitException.class)
    public ResponseResult<String> handle(RedisLimitException e){
        log.error("Redis限流异常:-------------->{}", e.getDefaultMessage());
        return ResponseResult.fail(e.getCode(), e.getDefaultMessage());
    }

    /**
     * 校验异常
     *  see: https://blog.csdn.net/wangooo/article/details/114288458
     */
    @ExceptionHandler(value = BindException.class)
    public ResponseResult<Object> handle(BindException e){
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder errorMessage = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            //errorMessage += fieldError.getDefaultMessage();
            errorMessage.append(fieldError.getDefaultMessage());
        }
        log.error("校验异常:-------------->{}", errorMessage.toString());
        return ResponseResult.fail(ResponseCode.VALIDATION_EXCEPTION.getCode(), errorMessage.toString());
    }

    /**
     * 请求异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseResult<Object> handle(MissingServletRequestParameterException e){
        log.error("请求异常:-------------->{}",e.getMessage()); // Required request parameter 'uId' for method parameter type Integer is not present
        return ResponseResult.fail(ResponseCode.REQUEST_EXCEPTION.getCode(),ResponseCode.REQUEST_EXCEPTION.getMessage());
    }

    /**
     * sql异常
     */
    @ExceptionHandler(SQLException.class)
    public ResponseResult<Object> handle(SQLException e){
        log.error("SQL异常:-------------->{}",e.getMessage());
        return ResponseResult.fail(ResponseCode.ERROR.getCode(),"sql异常");
    }

    /**
     * 类型转换异常
     */
    @ExceptionHandler(value = ClassCastException.class)
    public void handle(ClassCastException e){
        log.error("类型转换异常:-------------->{}", e.getMessage());
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(value = NullPointerException.class)
    public ResponseResult<String> handle(NullPointerException e){
        e.printStackTrace();
        log.error("空指针异常:-------------->{}", e.getMessage());
        return ResponseResult.fail(ResponseCode.ERROR.getCode(), Constant.ERROR_MSG);
    }

    /**
     * io异常
     */
    @ExceptionHandler(value = IOException.class)
    public ResponseResult<String> handle(IOException e){
        e.printStackTrace();
        log.error("io异常:-------------->{}", e.getMessage());
        return ResponseResult.fail(ResponseCode.ERROR.getCode(), Constant.ERROR_MSG);
    }

    /**
     * 运行异常
     */
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseResult<Object> handle(RuntimeException e){
        log.error("运行时异常:-------------->{}",e.getMessage());
        return ResponseResult.fail(ResponseCode.ERROR.getCode(), Constant.ERROR_MSG);
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseResult<Object> handle(Exception e){
        log.error("系统异常:-------------->{}",e.getMessage());
        return ResponseResult.fail(ResponseCode.SYSTEM_FAIL.getCode(), Constant.ERROR_MSG);
    }

}
