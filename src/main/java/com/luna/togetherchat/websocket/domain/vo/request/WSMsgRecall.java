package com.luna.togetherchat.websocket.domain.vo.request;

import lombok.Data;

/**
 * Description:消息撤回的推送类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Data
public class WSMsgRecall{
    private Long msgId;
    private Long roomId;
    //撤回的用户
    private Long recallUid;

}
