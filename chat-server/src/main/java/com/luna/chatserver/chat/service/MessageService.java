package com.luna.chatserver.chat.service;

import com.luna.chatserver.chat.domain.entity.Message;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author starrybamboo
 * @since 2025-03-26
 */
public interface MessageService{
    void savePushMsg(Message msg);

    void changePushMsg(Message msg);
}
