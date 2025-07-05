package com.luna.togetherchat.call.service;

import com.luna.togetherchat.call.domain.entity.Session;
import com.luna.togetherchat.call.domain.request.*;
import com.luna.togetherchat.call.domain.response.SessionInfoResponse;
import com.luna.togetherchat.call.domain.response.SessionResponse;

import java.util.List;

public interface CallService {
    /**
     * 发起通话
     * @param request 通话请求
     * @param callerId 发起者ID
     * @return 通话会话信息
     */
    SessionInfoResponse initiateCall(CallingRequest request, Long callerId);

    /**
     * 结束通话
     * @param request 结束请求
     * @param userId 用户ID
     */
    void endCall(CallingCancelRequest request, Long userId);

    /**
     * 获取通话历史
     * @param userId 用户ID
     * @return 通话历史分页结果
     */
    List<SessionResponse> getCallHistory(Long userId);
}