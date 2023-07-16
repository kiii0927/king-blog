package com.king.api.web.controller.Tool;

import com.king.core.aspectj.preven.Prevent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author king
 * @version 1.0
 * @since 2023-06-20
 **/
@RestController
@RequestMapping("/v1/test")
public class TestController {

    @GetMapping("/hello")
    public String HelloWorld(){
        return "HelloWorld";
    }


    @GetMapping("/01")
    @Prevent(message = "请勿重复请求" ,value = "10")
    public String test01(){
        return "ok";
    }

}
