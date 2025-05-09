package com.luna.togetherchat.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: web请求信息收集类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-04-05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestInfo {
    private Long userId;
    private String username;
    private String ip;
}
