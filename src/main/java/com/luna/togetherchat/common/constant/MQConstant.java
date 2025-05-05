package com.luna.togetherchat.common.constant;

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

    String MYSQL_CHANGE_QUEUE = "mysql.change.queue";
    String MYSQL_CHANGE_EXCHANGE = "mysql.change.exchange";


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
