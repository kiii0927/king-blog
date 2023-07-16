package com.king.sys.service.message;

import com.baomidou.mybatisplus.extension.service.IService;
import com.king.sys.bean.entity.message.SysMessage;
import com.king.sys.bean.entity.message.UserSysMessage;

import java.util.List;

/**
 * @author king
 * @version 1.0
 * @since 2023-07-11
 * @description 针对表【user_sys_message】的数据库操作Service
 **/
public interface IUserSysMessageService extends IService<UserSysMessage> {

    /**
     * 批量插入数据
     * @param key sys message key
     * @param list 最近登录的用户唯一标识
     * @return true/false
     */
    boolean insertBatch(String key, List<String> list, long source);

    /**
     * 批量插入数据
     * @param sysMessages sys message list
     * @param identify 用户身份
     * @return true/false
     */
    boolean insertBatch(List<SysMessage> sysMessages, String identify);

    /**
     * 是否存在
     * @param list sid list
     * @param identify 用户身份
     * @return true/false
     */
    boolean exists(List<String> list, String identify);

    /**
     * 查询消息是否已读<br>
     *  1:已读; 0:未读
     * @param uid user id
     * @param sid sys message id
     * @return true/false
     */
    boolean queryIsRead(String uid, String sid);

}
