package com.luna.togetherchat.call.service;

import com.luna.togetherchat.call.domain.request.*;
import com.luna.togetherchat.call.domain.response.CallHistoryResponse;
import com.luna.togetherchat.common.domain.vo.response.CursorPageBaseResponse;

public interface CallService {
//    /**
//     * 发起通话
//     * @param request 通话请求
//     * @param callerId 发起者ID
//     * @return 通话会话信息
//     */
//    void initiateCall(CallingRequest request, Long callerId);
//
//    /**
//     * 接受通话
//     * @param request 接受请求
//     * @param receiverId 接收者ID
//     * @return 通话会话信息
//     */
//    void acceptCall(CallingAcceptRequest request, Long receiverId);
//
//    /**
//     * 拒绝通话
//     * @param request 拒绝请求
//     * @param receiverId 接收者ID
//     */
//    void rejectCall(CallingRejectRequest request, Long receiverId);
//
//    /**
//     * 结束通话
//     * @param request 结束请求
//     * @param userId 用户ID
//     */
//    void endCall(CallingCancelRequest request, Long userId);
//
//    /**
//     * 获取通话历史
//     * @param userId 用户ID
//     * @param cursor 游标
//     * @param pageSize 页大小
//     * @return 通话历史分页结果
//     */
//    CursorPageBaseResponse<CallHistoryResponse> getCallHistory(Long userId, Long cursor, Integer pageSize);
}