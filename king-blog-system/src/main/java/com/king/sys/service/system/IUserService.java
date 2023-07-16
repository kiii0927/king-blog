package com.king.sys.service.system;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.king.common.module.constant.Constant;
import com.king.common.utils.CodeUtil;
import com.king.sys.bean.dto.EmailDto;
import com.king.sys.bean.dto.LoginDto;
import com.king.sys.bean.dto.UserRegisterDto;
import com.king.sys.bean.dto.VerifyCodeDto;
import com.king.sys.bean.entity.system.User;
import com.king.sys.bean.vo.UserVo;
import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *    用户表 服务类
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-06-29
 **/
public interface IUserService extends IService<User> {

    /**
     * 发送邮箱验证码
     * @param email 接收人
     * @param from  发送者
     */
    void sendEmailCode(String email, String from);

    /**
     * 保存用户
     * @param userRegisterDto 用户注册DTO {@link UserRegisterDto}
     * @return {@link User}
     */
    User saveUser(UserRegisterDto userRegisterDto);

    /**
     * 通过指定字段获取用户数据
     * @param column 字段名
     * @return {@link User}
     */
    default User getUserByEmail(@NonNull Object column){
        return this.getOne(new LambdaQueryWrapper<User>().eq(User::getEmail, column));
    }

    /**
     * 根据 token 字段获取用户信息
     *
     * @param token 用户Token
     * @versions 1.0
     * @deprecated 该方法已弃用, {@link #getUserInfoByTokenPro(String)}取代该方法
     * @return {@link User}
     */
    @Deprecated
    User getUserInfoByToken(String token);

    /**
     * 根据 token 字段获取用户信息
     *    如果 t_user 表中没有, 则回去 t_auth_user 表中查询,
     *    再将查询带的数据转换成 User 实体类
     *    {@link com.king.sys.bean.entity.auth.AuthUser} 转换成 {@link User}
     * @param token 用户Token
     * @versions 1.1
     * @return {@link User}
     */
    @Nullable
    UserVo getUserInfoByTokenPro(String token);

    /**
     * 用户登录
     * @param loginDto
     * @return data
     */
    Map<String, Object> login(LoginDto loginDto);

    /**
     * 上传用户头像
     * @param token 用户唯一标识
     * @param file {@link MultipartFile}
     * @return url
     */
    String uploadAvatar(String token, MultipartFile file) throws IOException;

    /**
     * 修改用户登录时间
     * @param identify 用户唯一标识
     */
    void updateLoginTime(String identify);

    /**
     * 修改用户登录时间
     * @param wrapper 修改条件
     */
    void updateLoginTime(LambdaUpdateWrapper<User> wrapper);

    /**
     * 修改头像
     * @param url 头像新地址
     * @param token 用户唯一标识
     */
    void updateAvatar(String url, String token);

    /**
     * 用户留言
     * @param emailDto {@link EmailDto}
     * @param email 接收/发送的邮箱
     */
    void leaveWord(EmailDto emailDto, String email);

    /**
     * 是否登录<br/>
     *  验证jwt是否存在 or 已失效
     * @param jwt jwt
     * @return
     */
    Map<String, Object> isLogin(String jwt);

    /**
     * 获取图片验证码
     * @deprecated {@link #getImgCode()}
     * @param response {@link HttpServletResponse}
     * @throws IOException
     */
    default void getCode(HttpServletResponse response) throws IOException{
        CodeUtil.createImgCode(response);
    }

    /**
     * 给用户添加唯一标识
     * @return string
     */
    default String addIdentify(){
        return IdUtil.fastSimpleUUID();
    }

    /**
     * 重置用户密码
     * @param vcd {@link VerifyCodeDto}
     */
    void resetPassword(VerifyCodeDto vcd);

    /**
     * 获取图片验证码
     * @return map
     */
    default Map<String, String> getImgCode(){
        return CodeUtil.createImgCode();
    }


    /**
     * 查询最近登录的用户<br/>
     *  - 通过 {@link Constant#OFFSET_NUMBER} 字段查询偏移的的天数
     * @return list id
     */
    List<String> queryLastLoginUser();
}
