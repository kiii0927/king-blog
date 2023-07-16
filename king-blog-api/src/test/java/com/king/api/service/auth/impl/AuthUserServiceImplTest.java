package com.king.api.service.auth.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.king.sys.bean.entity.auth.AuthUser;
import com.king.sys.service.auth.IAuthUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class AuthUserServiceImplTest {

    @Autowired
    private IAuthUserService authUserService;

    @Test
    void test01(){

        System.out.println("authUserService = " + authUserService);
        //AuthUser authUser = AuthUser.builder().nickname("sweets").username("k_sweets").build();
        //
        ////boolean update = authUserService.update(Wrappers.lambdaUpdate(AuthUser.class)
        ////        .eq(AuthUser::getSource, "GITEE").eq(AuthUser::getUuid, "9296100")
        ////        .set(AuthUser::getNickname, "sweets").set(AuthUser::getUsername, "k_sweets"));
        //
        //boolean update = authUserService.update(authUser, Wrappers.lambdaUpdate(AuthUser.class)
        //        .eq(AuthUser::getSource, "GITEE").eq(AuthUser::getUuid, "9296100"));
        //
        //System.out.println("update = " + update);

    }

}
