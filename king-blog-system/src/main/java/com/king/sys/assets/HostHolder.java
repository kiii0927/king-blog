package com.king.sys.assets;

import com.king.sys.bean.vo.UserVo;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * <p>
 *    利用ThreadLocal存储用户登录的信息
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-07-16
 **/
@Component
public class HostHolder {

    private final ThreadLocal<UserVo> users = new ThreadLocal<>();

    public HostHolder(){

    }

    public UserVo getUser() {
        return users.get();
    }

    public void setUser(UserVo user) {
        users.set(user);
    }

    public void clear() {
        Optional.of(users).ifPresent(ThreadLocal::remove);
    }

    /**
     * 获取用户唯一标识
     * @return string
     */
    @Nullable
    public String getIdentify(){
        return Optional.ofNullable(this.getUser())
                .map(UserVo::getToken)
                .orElseGet(null);
    }
}
