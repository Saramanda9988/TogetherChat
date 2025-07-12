package com.luna.webrtcserver.common.constant;

/**
 * @author zhongzb create on 2021/06/10
 */
public interface MQConstant {

    String SEND_MSG_EXCHANGE = "chat.send.msg.exchange";

    /**
     * push用户
     */
    String PUSH_EXCHANGE = "websocket.push.exchange";
    String PUSH_QUEUE = "websocket.push.queue";

    /**
     * flink交换机
     */
    String MYSQL_CHANGE_QUEUE = "mysql.change.queue";
    String MYSQL_CHANGE_EXCHANGE = "mysql.change.exchange";

    /**
     * 消息保存相关交换机
     */
    String MESSAGE_SAVE_QUEUE = "message.save.queue";
    String MESSAGE_SAVE_EXCHANGE = "message.save.exchange";
    String MESSAGE_SAVE_ROUTING_KEY = "message.save.#"; // 监听所有以 message.save. 开头的路由键
    String MESSAGE_SAVE_KEY = "message.save";

    String MESSAGE_CHANGE_QUEUE = "message.change.queue";
    String MESSAGE_CHANGE_EXCHANGE = "message.change.exchange";
    String MESSAGE_CHANGE_ROUTING_KEY = "message.change.#"; // 监听所有以 message.change. 开头的路由键
    String MESSAGE_CHANGE_KEY = "message.change";

    /**
     * (授权完成后)登录信息mq
     */
    String LOGIN_MSG_TOPIC = "user_login_send_msg";
    String LOGIN_MSG_GROUP = "user_login_send_msg_group";

    /**
     * 扫码成功 信息发送mq
     */
    String SCAN_MSG_TOPIC = "user_scan_send_msg";
    String SCAN_MSG_GROUP = "user_scan_send_msg_group";
}
