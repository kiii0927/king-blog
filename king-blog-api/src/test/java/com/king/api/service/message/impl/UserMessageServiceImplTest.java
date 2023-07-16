package com.king.api.service.message.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.king.sys.bean.entity.message.UserMessage;
import com.king.sys.mapper.UserMessageMapper;
import com.king.sys.service.message.IUserMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserMessageServiceImplTest {

    @Autowired
    private IUserMessageService userMessageService;

    @Autowired
    private UserMessageMapper userMessageMapper;

    @Test
    void query01(){
        String toId = "1542229726042370048";
        LambdaQueryWrapper<UserMessage> wrapper = Wrappers.lambdaQuery(UserMessage.class)
                .eq(UserMessage::getToId, toId);

        //List<UserMessage> list = userMessageService.list(wrapper);

        List<UserMessage> list = userMessageMapper.selectMyList(wrapper);

        list.forEach(System.out::println);
    }
}
