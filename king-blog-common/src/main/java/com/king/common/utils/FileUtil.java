package com.king.common.utils;

import cn.hutool.core.img.Img;
import com.king.common.module.constant.Constant;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * <p>
 *    文件工具类
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-06-21
 **/
public final class FileUtil {

    private FileUtil(){
        throw new AssertionError("No com.king.common.utils.FileUtil instances for you!");
    }

    /**
     * 上传头像并返回该url
     * @param file 上传的文件
     * @return 文件url
     */
    public static String uploadAvatar(MultipartFile file) throws IOException {

        // 文件后缀, 如 jpeg,png,jpg,gif
        final String fileSuffix = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf('.') + 1);

        // 文件名
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + fileSuffix;

        // 获取 file
        File descFile = getImgDirFile(fileName);

        System.out.println("descFile.getPath() = " + descFile.getPath());

        // 文件写入
        file.transferTo(descFile);

        // 文件压缩
        Img.from(descFile).setQuality(0.8)//压缩比率
                .write(descFile);

        // 文件url
        return Constant.BASE_UPLOAD_URl + fileName;
    }

    /**
     * 获取 file
     * @param fileName file name. uuid
     * @return file
     * @throws FileNotFoundException
     */
    public static File getImgDirFile(String fileName) throws FileNotFoundException {
        //String basePath = ResourceUtils.getURL("static").getPath();
        //File descFile = new File(basePath + Constant.DEFAULT_AVATAR_UPLOAD_PATH, fileName);
        File descFile = new File(Constant.DEFAULT_AVATAR_UPLOAD_PATH, fileName);
        if (!descFile.exists()) {
            boolean flag = descFile.mkdirs();
            if (!flag) {
                throw new FileNotFoundException("文件夹不存在,自动创建失败！");
            }
        }
        return new File(descFile.getAbsolutePath());
    }
}
