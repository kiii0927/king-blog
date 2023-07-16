package com.king.sys.bean.factory;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import com.king.common.module.constant.Constant;
import com.king.sys.bean.entity.auth.AuthUser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * @author king
 * @version 1.0
 * @since 2023-06-27
 **/
public final class JSONObjectFactory {

    private JSONObjectFactory(){}

    public static JSONObject getJSONObject(AuthUser authUser) throws UnsupportedEncodingException {
        return new JSONObject(MapUtil.builder(new HashMap<String, Object>())
                .put("auth_id", authUser.getId())
                .put("uuid", authUser.getUuid())
                .put("username", URLEncoder.encode(authUser.getUsername(), "UTF-8"))
                .put("nickname", URLEncoder.encode(authUser.getNickname(), "UTF-8"))
                .put("token", authUser.getToken())
                .put("gender", authUser.getGender())
                .put("avatar", authUser.getAvatar())
                .put("email", authUser.getEmail())
                .put("permission", "test")
                .put("isAuthLogin", true)
                .put("createTime", authUser.getCreateTime())
                .put("loginTime", authUser.getLoginTime())
                .build(),false,true)
                .setDateFormat(Constant.DEFAULT_DATE_FORMAT_PATTERN);
    }
}
