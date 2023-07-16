package com.king.api.web.controller;

import com.king.common.module.domain.ResponseResult;
import com.king.sys.service.system.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * <p>
 *    文件上传控制器
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-06-29
 **/
@RestController
@RequestMapping("/v1/upload")
public class FileController {

    @Autowired
    private IUserService userService;

    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    public ResponseResult<String> uploadAvatar(MultipartFile file, @RequestParam(name = "token") String token) throws IOException {
        return ResponseResult.success(this.userService.uploadAvatar(token, file));
    }
}
