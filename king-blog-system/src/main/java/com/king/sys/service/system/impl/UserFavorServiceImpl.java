package com.king.sys.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.common.exception.ServiceException;
import com.king.common.exception.WrongInputException;
import com.king.common.module.domain.ResponseCode;
import com.king.sys.bean.dto.UserFavorDto;
import com.king.sys.bean.entity.system.UserFavor;
import com.king.sys.bean.vo.FavorVo;
import com.king.sys.mapper.UserFavorMapper;
import com.king.sys.service.system.IUserFavorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-05
 **/
@Slf4j
@Service
public class UserFavorServiceImpl extends ServiceImpl<UserFavorMapper, UserFavor> implements IUserFavorService {

    @Autowired
    private UserFavorMapper userFavorMapper;

    @Transactional
    @Override
    public void saveUserFavor(UserFavorDto userFavorDto) {
        UserFavor userFavor = this.userFavorMapper.selectOne(Long.parseLong(userFavorDto.getBlogId()), userFavorDto.getIdentify());
        // 有就修改状态,没有就添加
        if(userFavor != null){
            this.userFavorMapper.updateDeletedById(userFavor.getId());
        }else {
            int insert = this.userFavorMapper.insert(UserFavor.builder()
                    .bId(Long.parseLong(userFavorDto.getBlogId())).identify(userFavorDto.getIdentify()).build());
            if (insert <= 0) throw new ServiceException(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getMessage());
        }
    }

    @Override
    public boolean isFavor(String id) {
        UserFavor favor = this.getById(id);
        return null != favor;
    }

    @Override
    public boolean isFavor(String identify, String bid) {
        if (ObjectUtils.isEmpty(identify)) {
            return false;
        }
        // 创建 wrapper
        LambdaQueryWrapper<UserFavor> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(UserFavor::getIdentify, identify)
                .eq(UserFavor::getBId, Long.parseLong(bid));

        return this.userFavorMapper.exists(wrapper);
    }

    @Override
    public List<FavorVo> selectAllByIdentify(String identify) {
        return this.userFavorMapper.findAllByIdentify(identify);
    }

    @Override
    public List<FavorVo> deleteFavorByIdAndBlogId(UserFavorDto userFavorDto) {
        return Optional.ofNullable(this.deleteFavorByIdAndBlogId(userFavorDto.getIdentify(), userFavorDto.getBlogId()))
                .orElseThrow(WrongInputException::new);
        //return this.deleteFavorByIdAndBlogId(userFavorDto.getIdentify(), userFavorDto.getBlogId());
    }

    @Transactional
    @Override
    public List<FavorVo> deleteFavorByIdAndBlogId(String identify, String bid) {
        Objects.requireNonNull(identify, "identify 值为null");
        Objects.requireNonNull(bid, "bid 值为null");

        // 创建wrapper
        LambdaQueryWrapper<UserFavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavor::getIdentify, identify).eq(UserFavor::getBId, Long.parseLong(bid));

        // 执行sql操作
        UserFavor userFavor = this.getOne(wrapper);
        Assert.notNull(userFavor, ResponseCode.ERROR.getMessage());
        this.remove(wrapper);

        return this.selectAllByIdentify(identify);
    }
}
