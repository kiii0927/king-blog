package com.king.sys.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.exception.ServiceException;
import com.king.common.exception.WrongInputException;
import com.king.common.module.domain.ResponseCode;
import com.king.common.utils.CodeUtil;
import com.king.sys.bean.dto.VerifyCodeDto;
import com.king.sys.bean.entity.system.User;
import com.king.sys.bean.entity.system.Validation;
import com.king.sys.bean.enums.LoginSignEnum;
import com.king.sys.mapper.UserMapper;
import com.king.sys.mapper.ValidationMapper;
import com.king.sys.service.system.IValidationService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * <p>
 *    服务实现类
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-03
 **/
@Service
public class ValidationServiceImpl extends ServiceImpl<ValidationMapper, Validation> implements IValidationService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ValidationMapper validationMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveCode(String email, String code, Integer type, Date offsetMinute) {
        // 构建 validation 类
        Validation validation = Validation.builder()
                .email(email).code(code)
                .type(LoginSignEnum.getRealType(type))
                .time(offsetMinute)
                .build();
        this.saveCode(validation);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveCode(Validation validation) {
        validationMapper.addCode(validation);
    }

    @Override
    public boolean validationEmailIsExist(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return userMapper.exists(wrapper);
    }


    @Override
    public boolean validationUserIsExist(String username) {
        return userMapper.exists(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    @Override
    public boolean validationEmailCode(VerifyCodeDto validationCodeDto) {
        Date now = new Date();
        // 创建wrapper
        LambdaQueryWrapper<Validation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Validation::getEmail, validationCodeDto.getEmail())
                .eq(Validation::getCode, validationCodeDto.getCode())
                .eq(Validation::getType, LoginSignEnum.REGISTER.getType());

        boolean exists = validationMapper.exists(wrapper);
        // 没有找到该邮箱的code, 表示不存在, 验证码错误
        if(!exists){
            throw new ServiceException(ResponseCode.ERROR.getCode(), "注册失败，验证码错误！");
        }
        wrapper.ge(Validation::getTime, now);
        return validationMapper.exists(wrapper);
    }

    @Override
    public boolean validationEmailCode(String email, Date now) {
        LambdaQueryWrapper<Validation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Validation::getEmail, email)
                .eq(Validation::getType, LoginSignEnum.REGISTER.getType())
                .ge(Validation::getTime, now);
        return validationMapper.exists(wrapper);
    }

    @Override
    public void validationImgCode(@NonNull VerifyCodeDto vcd, HttpServletRequest request) {
        String identify = request.getHeader("identify");
        boolean verify = CodeUtil.verifyCode(identify, vcd.getCode());
        if (!verify) {
            throw new WrongInputException("验证码不正确");
        }
        boolean isExist = this.validationEmailIsExist(vcd.getEmail());
        if (!isExist){
            throw new WrongInputException("该邮箱未注册过");
        }
    }
}
