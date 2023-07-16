package com.king.sys.service.message.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.king.sys.bean.entity.message.SysMessage;
import com.king.sys.bean.entity.message.UserSysMessage;
import com.king.sys.mapper.UserSysMessageMapper;
import com.king.sys.service.message.IUserSysMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author k
 * @description 针对表【user_sys_message】的数据库操作Service实现
 * @createDate 2023-04-03 09:54:15
*/
@Service
@DS("slave")
public class UserSysMessageServiceImpl extends ServiceImpl<UserSysMessageMapper, UserSysMessage>
    implements IUserSysMessageService {

    @Autowired
    private UserSysMessageMapper userSysMessageMapper;

    @Override
    //@DSTransactional
    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(String key, List<String> list, long source) {
        List<UserSysMessage> userSysMessages = new ArrayList<>(list.size());
        list.forEach(item -> {
            UserSysMessage userSysMessage = UserSysMessage.builder()
                    .sysId(key)
                    .uid(item)
                    .isRead(0)
                    .source(String.valueOf(source))
                    .opeTime(LocalDateTime.now())
                    .build();
            userSysMessages.add(userSysMessage);
        });
        // insert batch
        return super.saveBatch(userSysMessages);
    }

    @Override
    //@DSTransactional
    @Transactional(rollbackFor = Exception.class)
    public boolean insertBatch(List<SysMessage> sysMessages, String identify) {
        List<UserSysMessage> userSysMessages = new ArrayList<>(sysMessages.size());
        sysMessages.forEach(item -> {
            UserSysMessage userSysMessage = UserSysMessage.builder()
                    .sysId(item.getKey())
                    .uid(identify)
                    .isRead(1)
                    .source(this.toEpochMilli(item.getCreateTime()))
                    .createTime(item.getCreateTime())
                    //.ope_time(LocalDateTime.now())
                    .build();
            userSysMessages.add(userSysMessage);
        });
        // insert batch
        return super.saveBatch(userSysMessages);
    }

    @Override
    public boolean exists(List<String> list, String identify){
        // 创建 wrapper
        LambdaQueryWrapper<UserSysMessage> wrapper = Wrappers.lambdaQuery(UserSysMessage.class)
                .eq(UserSysMessage::getUid, identify)
                .in(UserSysMessage::getSysId, list);

        return this.userSysMessageMapper.exists(wrapper);
    }

    @Override
    public boolean queryIsRead(@NotNull String uid, @NotNull String sid) {
        return userSysMessageMapper.exists(Wrappers.lambdaQuery(UserSysMessage.class)
                .eq(UserSysMessage::getUid, uid).eq(UserSysMessage::getSysId, sid)
                .eq(UserSysMessage::getIsRead, 1)
        );
    }

    /**
     * localDateTime 转 毫秒级别时间戳
     * @param dateTime localDateTime
     * @return 毫秒级别时间戳
     */
    public String toEpochMilli(LocalDateTime dateTime){
        return String.valueOf(dateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
    }
}




