package com.king.sys.service.auth.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.exception.ServiceException;
import com.king.common.module.domain.ResponseCode;
import com.king.sys.bean.dto.UserInfoDto;
import com.king.sys.bean.factory.JSONObjectFactory;
import com.king.sys.bean.factory.UserInfoDtoFactory;
import com.king.sys.bean.entity.auth.AuthUser;
import com.king.sys.bean.enums.GenderEnum;
import com.king.sys.mapper.AuthUserMapper;
import com.king.sys.service.auth.IAuthUserService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * 第三方授权表 服务实现类
 * </p>
 * @author: king
 * @create: 2023-01-04 20:33
 **/
@Slf4j
@Service
public class AuthUserServiceImpl extends ServiceImpl<AuthUserMapper, AuthUser> implements IAuthUserService {

    @Autowired
    private AuthUserMapper authUserMapper;

    //@Autowired
    //private UserInfoMapper userInfoMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;


    @Override
    public JSONObject login(AuthResponse<Object> login) throws UnsupportedEncodingException {
        AuthUser authUser = this.addUser(login);

        // 传给前端的数据
        return this.getResponseResult(authUser);
    }

    @Override
    public AuthUser addUser(AuthResponse<Object> login) {
        Objects.requireNonNull(login, "The login parameter can't be null");
        // 第三方登录失败, 直接抛出异常
        if (!login.ok()) {
            log.warn("第三方登录失败：{}",login.getMsg());
            throw new ServiceException(ResponseCode.LOGIN_FAIL.getCode(), "登录失败");
        }
        // 拿到第三方登录平台的用户数据
        Object res = login.getData();

        return this.insertUserAndGetUser(this.createJSONObject(res));
    }

    @Override
    public AuthUser getUser(String token) {
        LambdaQueryWrapper<AuthUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthUser::getToken, token);
        return this.getOne(wrapper);
    }

    /**
     * 插入用户数据并获取
     * @param jsonObject  第三方授权登录用户数据 {@link JSONObject}
     * @return {@link AuthUser}
     */
    public AuthUser insertUserAndGetUser(JSONObject jsonObject){
        String uuid = jsonObject.getStr("uuid");
        String source = jsonObject.getStr("source");
        String email = jsonObject.getStr("email");
        String avatar = jsonObject.getStr("avatar");

        AtomicReference<AuthUser> authUser = new AtomicReference<AuthUser>();
        //TransactionStatus transaction = this.transactionManager.getTransaction(new DefaultTransactionAttribute());

        // 该用户存在直接登录, 否则注册
        if (!this.isAuthUserExists(uuid, source)) {
            transactionTemplate.execute(status -> {
                UserInfoDto userInfoDto = UserInfoDtoFactory.getUserInfoDto(jsonObject);

                // create a authUser entity
                GenderEnum realGender = GenderEnum.getRealGender(userInfoDto.getGender());
                System.out.println("realGender = " + realGender);
                authUser.set(AuthUser.builder()
                        .uuid(uuid).username(userInfoDto.getUsername())
                        .nickname(userInfoDto.getNickname()).gender(realGender)
                        .avatar(avatar).email(email)
                        .source(source)
                        .build());

                authUserMapper.insert(authUser.get());

                //String token = authUserMapper.findTokenByUuidAndSource(uuid, source.toUpperCase());
                //userInfoDto.setToken(token);

                //System.out.println("authUser = " + authUser.get());

                //// user_info 表也添加
                //this.userInfoMapper.insert(userInfoDto, realGender);

                return Boolean.TRUE;
            });
        }else {
            String username = jsonObject.getStr("username");
            String nickname = jsonObject.getStr("nickname");

            // 直接登录, 修改 data
            this.update(Wrappers.lambdaUpdate(AuthUser.class)
                    .eq(AuthUser::getSource, source).eq(AuthUser::getUuid, uuid)
                    .set(AuthUser::getNickname, nickname).set(AuthUser::getUsername, username)
                    .set(AuthUser::getEmail, email).set(AuthUser::getAvatar, avatar)
                    .set(AuthUser::getLoginTime, LocalDateTime.now()));

            authUser.set(this.getAuthUser(uuid, source));
        }
        return authUser.get();
    }

    @Override
    public AuthUser getAuthUser(String uuid, String source){
        // 校验参数是否合法
        Assert.notNull(uuid, "uuid" + ResponseCode.PARAM_IS_BLANK.getMessage());
        Assert.notNull(source, "source" + ResponseCode.PARAM_IS_BLANK.getMessage());

        AuthUser authUser = this.authUserMapper.selectOne(new LambdaQueryWrapper<AuthUser>()
                .eq(AuthUser::getUuid, uuid)
                .eq(AuthUser::getSource, source));
        Assert.notNull(authUser, "获取第三方用户信息失败");

        return authUser;
    }


    /**
     * 通过 {@link Optional} 创建一个 {@link JSONObject} 对象
     * @param res 第三方登录平台的用户数据
     * @return {@link JSONObject}
     */
    private JSONObject createJSONObject(Object res) {
        // 创建一个 Optional 实例，但当 t为null时不会抛出异常，而是返回一个空的实例
        return Optional.ofNullable(res)
                // 将响应的数据格式化为字符串json
                .map(JSONUtil::toJsonStr)
                // 将字符串转化为json对象
                .map(JSONUtil::parseObj)
                // 为空则抛出异常
                .orElseThrow(() -> new NullPointerException("第三方用户登录授权数据为空"));
    }


    @Override
    public JSONObject getResponseResult(AuthUser authUser) throws UnsupportedEncodingException {
        Objects.requireNonNull(authUser, "The authUser parameter can't be null");
        // 返回给前端的数据
        return JSONObjectFactory.getJSONObject(authUser);
    }

    @Override
    public void remove(String uuid, String source) {
        this.remove(new LambdaQueryWrapper<AuthUser>()
                .eq(AuthUser::getUuid, uuid)
                .eq(AuthUser::getSource, source));
    }

    @Override
    public boolean isAuthUserExists(@NonNull String uuid, @NonNull String source) {
        // 可以用uuid + source唯一确定一个用户
        return this.authUserMapper.exists(new LambdaQueryWrapper<AuthUser>()
                .eq(AuthUser::getUuid, uuid)
                .eq(AuthUser::getSource, source));
    }
}
