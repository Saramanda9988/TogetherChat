package com.luna.togetherchat.common.domain.vo.request.websocket;

import lombok.Data;

/**
 * Description: websocket前端请求体
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Data
public class WSBaseReq {
    /**
     * 请求类型 1.请求登录二维码，2心跳检测
     */
    private Integer type;

    private String data;
}
