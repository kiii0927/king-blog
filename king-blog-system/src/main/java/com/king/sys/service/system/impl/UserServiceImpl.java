package com.king.sys.service.system.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.exception.ServiceException;
import com.king.common.exception.WrongInputException;
import com.king.common.module.constant.Constant;
import com.king.common.module.domain.ResponseCode;
import com.king.common.module.domain.TransactionConsumer;
import com.king.common.utils.*;
import com.king.sys.bean.dto.*;
import com.king.sys.bean.factory.UserFactory;
import com.king.sys.bean.entity.auth.AuthUser;
import com.king.sys.bean.enums.LoginSignEnum;
import com.king.sys.bean.entity.system.User;
import com.king.sys.bean.entity.system.Validation;
import com.king.sys.bean.vo.UserVo;
import com.king.sys.mapper.AuthUserMapper;
import com.king.sys.mapper.UserMapper;
import com.king.sys.service.system.IUserService;
import com.king.sys.service.system.IValidationService;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * <p>
 *    用户表 服务实现类
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-06-29
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final UserMapper userMapper;

    private final AuthUserMapper authUserMapper;

    private final IValidationService validationService;

    //private final UserInfoMapper userInfoMapper;

    // SpringBoot自动装配的邮件发送实现类
    private final JavaMailSender javaMailSender;

    private final TransactionTemplate transactionTemplate;

    @Override
    public void sendEmailCode(String email, String from) {
        if(StrUtil.isBlank(email)) {
            throw new ServiceException(ResponseCode.PARAM_IS_BLANK.getCode(),ResponseCode.PARAM_IS_BLANK.getMessage());
        }
        Date now = new Date();
        boolean isExist = validationService.validationEmailCode(email, now);
        if(isExist) {
            throw new ServiceException(ResponseCode.ERROR.getCode(), "当前您的验证码仍然有效，请不要重复发送");
        } else {
            sendMail(email,from,now);
        }
    }

    @Async("kingAsyncExecutor") //添加该注解  代表该方法异步执行
    @Retryable(value = MailException.class, maxAttempts = 4, backoff = @Backoff(delay = 2000, multiplier = 0))
    public void sendMail(String email, String from, Date now){
        long start = System.currentTimeMillis();
        // 发送邮箱
        String code = RandomUtil.randomNumbers(6); // 随机一个 6 位长度的验证码
        // 邮件内容
        String content = "您本次注册的验证码是：" + code + "，有效期5分钟。请妥善保管，切勿泄露";

        Try.of(() -> {
            // 获取邮件实例模板
            SimpleMailMessage message = EmailUtil.simple(from, email, content);
            // 发送邮寄
            javaMailSender.send(message); // 发送邮寄
            long end = System.currentTimeMillis();
            log.info("发送所消耗的时间：{}", (end - start) / 1000);
            return "ok";
        })
                .onSuccess(v1 -> {
                    // 发送成功后, 把验证码存到数据库
                    TransactionConsumer transactionConsumer = () ->
                            validationService.saveCode(Validation.builder()
                                    .email(email).code(code)
                                    .type(LoginSignEnum.REGISTER)
                                    .time(DateUtil.offsetMinute(now,5))
                                    .build());
                    transactionConsumer.doInTransaction();
                })
                .onFailure(v2 -> {
                    v2.printStackTrace();
                    log.error("邮件发送失败: {}", v2.getMessage());
                });
    }

    @Override
    public synchronized User saveUser(UserRegisterDto userRegisterDto) {
        AtomicReference<User> user = new AtomicReference<>(new User());

        transactionTemplate.execute(status -> {

            user.set(UserFactory.getUser(userRegisterDto));
            int insert = this.userMapper.insert(user.get());

            if (insert <= 0){
                throw new ServiceException(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getMessage());
            }

            //// user info
            //UserInfoDto userInfoDto = UserInfoDtoFactory.getUserInfoDto(user.get());
            //this.userInfoMapper.insert(userInfoDto, GenderEnum.getRealGender(userInfoDto.getGender()));

            return Boolean.TRUE;
        });

        return Optional.ofNullable(user.get())
                .orElseThrow(ServiceException::new);
    }

    @Override
    public User getUserInfoByToken(String token) {
        // 创建wrapper
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getToken, token);

        // 执行操作
        //User user = userMapper.selectOne(wrapper);
        //Assert.notNull(user, ResultCode.ERROR.getMessage());

        return Optional.ofNullable(userMapper.selectOne(wrapper))
                .orElseThrow(() -> new IllegalArgumentException(ResponseCode.ERROR.getMessage()));
    }

    @Override
    public UserVo getUserInfoByTokenPro(String token) {

        final UserVo[] userVo = {null};

        // t_user 表查询不到, 需要去 t_auth_login
        User user = userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getToken, token));

        Try.success(user)
                .filter(Objects::nonNull)
                .onFailure(of -> {
                    log.info("onFailure: {}", of.getMessage());
                    AuthUser authUser = authUserMapper.selectOne(Wrappers.lambdaQuery(AuthUser.class)
                            .eq(AuthUser::getToken, token));

                    Optional.ofNullable(EntityUtil.toObj(authUser, UserVo::new))
                            .ifPresent(o -> userVo[0] = o);
                })
                .onSuccess(os -> {
                    Optional.ofNullable(EntityUtil.toObj(user, UserVo::new))
                            .ifPresent(o -> userVo[0] = o);
                });

        return userVo[0];
    }


    @Override
    public Map<String, Object> login(LoginDto loginDto) {
        // 拿到加密后的密码
        String pass = MD5Util.inputPassToDBPass(loginDto.getPassword(), Constant.SALT);

        // 构造 map (查询条件)
        Map<SFunction<User, ?>, Object> map = new HashMap<SFunction<User, ?>, Object>() {
            private static final long serialVersionUID = 1L;
            {
                put(User::getUsername, loginDto.getUsername());
                put(User::getPassword, pass);
            }
        };

        // 创建 wrapper
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.allEq(map);

        // select
        User user = this.getOne(wrapper);
        Assert.notNull(user, "用户名或密码有误");

        // 禁止登录
        if (user.getStatus() == 0) {
            throw new ServiceException(ResponseCode.ERROR.getCode(), ResponseCode.USER_LOCK_ERROR.getMessage());
        }

        // execute async
        CompletableFuture.runAsync(() -> {
            // 事务操作
            transactionTemplate.execute(status -> {
                // user info
                //this.userInfoService.save(this.builderUserInfo(user));

                wrapper.set(User::getLoginTime, LocalDateTime.now());
                // 修改用户登录时间
                this.updateLoginTime(wrapper);

                return Boolean.TRUE;
            });
        });

        // 创建 JWT
        String JWT = JwtUtil.createToken(user.getToken());

        return MapUtil.builder(new HashMap<String, Object>())
                .put("jwt", JWT)
                .put("user", user)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLoginTime(String identify) {
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate(User.class)
                .eq(User::getToken, identify)
                .set(User::getLoginTime, LocalDateTime.now());
        this.updateLoginTime(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLoginTime(LambdaUpdateWrapper<User> wrapper) {
        boolean update = super.update(wrapper);
        if (!update) {
            log.warn("updateLoginTime failed");
            //throw new ServiceException(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMessage());
        }
    }

    @Override
    @Retryable(value = MailException.class, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 0))
    @Transactional(rollbackFor = Exception.class)
    public void updateAvatar(String url, String token){
        // 修改操作
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getToken, token);

        boolean update = this.update(User.builder()
                .avatar(url)
                .build(), wrapper);
        //if (!update) throw new ServiceException(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMessage());
        if (!update) throw new ServiceException(ResponseCode.ERROR.getCode(), "第三方用户不支持修改头像!");
    }

    @Override
    @Retryable(value = MailException.class, maxAttempts = 4, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    public void leaveWord(EmailDto emailDto, String email) {
        long start = System.currentTimeMillis();
        // 获取邮件实例模板
        SimpleMailMessage message = EmailUtil.simple(email, emailDto.getContent(), emailDto.getTitle());

        // 发送邮件
        CompletableFuture.supplyAsync(() -> {
            log.info("发送邮件");
            javaMailSender.send(message);
            long end = System.currentTimeMillis();
            log.info("发送成功,所消耗的时间：{}", (end - start) / 1000);
            return "ok";
        });
    }

    @Override
    public Map<String, Object> isLogin(String jwt) {
        UserVo userVo = new UserVo();
        boolean verify = JwtUtil.verifyToken(jwt);
        // 验证成功, 解析 jwt
        if (verify) {
            String v = JwtUtil.parseToken(jwt);
            userVo = this.getUserInfoByTokenPro(v);
        } else {
            log.error("无效 jwt.");
        }
        return MapUtil.builder(new HashMap<String, Object>())
                .put("isLogin", verify)
                .put("user", userVo)
                .build();
    }


    @Override
    public void resetPassword(VerifyCodeDto vcd) {
        String password = vcd.getPassword();
        // 拿到加密后的密码
        String pass = MD5Util.inputPassToDBPass(password, Constant.SALT);
        // 创建 wrapper
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();

        // 构造需要修改的字段以及值和修改条件
        wrapper.set(User::getOriginalPassword, password)
                .set(User::getPassword, pass)
                .eq(User::getEmail, vcd.getEmail());

        // update
        boolean update = super.update(wrapper);

        if (!update) {
            throw new WrongInputException("修改失败");
        }
    }

    @Override
    public String uploadAvatar(@NonNull String token, @NonNull MultipartFile file) throws IOException {

        // 拿到url
        String url = FileUtil.uploadAvatar(file);

        CompletableFuture.runAsync(() -> this.updateAvatar(url, token));

        return url;
    }

    @Override
    public List<String> queryLastLoginUser() {
        // 当前时间偏移
        LocalDateTime offset = LocalDateTimeUtil.offset(LocalDateTime.now(), Constant.OFFSET_NUMBER, ChronoUnit.DAYS);

        // 查询最近登录的用户的id
        List<String> users = this.userMapper.selectList(Wrappers.lambdaQuery(User.class)
                .select(User::getToken)
                .gt(User::getLoginTime, offset))
                // 转为 string
                .stream().map(User::getToken).collect(Collectors.toList());

        List<String> authUsers = this.authUserMapper.selectList(Wrappers.lambdaQuery(AuthUser.class)
                .select(AuthUser::getToken)
                .gt(AuthUser::getLoginTime, offset))
                .stream().map(AuthUser::getToken).collect(Collectors.toList());

        // 合并数组,转为 arrayList类型返回
        return new ArrayList<>(CollUtil.addAll(users, authUsers));
    }


    /**
     * 重试机制兜底方法<br/>
     * 当重试次数执行完还是失败就会执行该方法
     * @param e
     */
    @Recover
    private void recover(Exception e){
        log.info("重试失败,异常信息: {}", e.getMessage());
        e.printStackTrace();
    }

}
