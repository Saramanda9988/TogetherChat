package com.luna.togetherchat.websocket.handler;

import com.luna.togetherchat.call.domain.request.*;
import com.luna.togetherchat.call.service.CallService;
import com.luna.togetherchat.common.utils.RequestHolder;
import com.luna.togetherchat.websocket.domain.enums.CallingSignalEnum;
import com.luna.togetherchat.websocket.domain.vo.request.WSCallSignalingAction;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 通话信令处理器实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CallSignalingHandlerImpl implements SignalingHandler {
    
    private final CallService callService;
    
    @Override
    public void handleSignaling(WSCallSignalingAction signalingAction, Channel channel, Long callerId) {
        CallingSignalEnum callingSignalEnum = CallingSignalEnum.of(signalingAction.getType());
        log.info("收到WebRTC信令: type={}, caller={}, receiver={}",
                callingSignalEnum, callerId, signalingAction.getReceiverId());

        try {
            switch (callingSignalEnum) {
                case OFFER -> {
                    // 处理通话请求信令
                    CallingRequest request = CallRequestFactory.createCallingRequest(callerId, signalingAction);
                    callService.initiateCall(request, callerId);
                }
                case ACCEPT -> {
                    // 处理接受通话信令
                    CallingAcceptRequest request = CallRequestFactory.createAcceptRequest(callerId, signalingAction);
                    callService.acceptCall(request, callerId);
                }
                case REJECT -> {
                    // 处理拒绝通话信令
                    CallingRejectRequest request = CallRequestFactory.createRejectRequest(callerId, signalingAction);
                    callService.rejectCall(request, callerId);
                }
                case CANCEL -> {
                    // 处理取消通话信令
                    CallingCancelRequest request = CallRequestFactory.createCancelRequest(callerId, signalingAction);
                    callService.endCall(request, callerId);
                }
                default -> log.error("未知信令类型: {}", signalingAction.getType());
            }
        } finally {
            RequestHolder.remove();
        }
    }

    /**
     * 通话请求工厂类，负责创建各类通话请求对象
     */
    private static class CallRequestFactory {
        /**
         * 创建通话发起请求
         */
        public static CallingRequest createCallingRequest(Long callerId, WSCallSignalingAction signalingAction) {
            return CallingRequest.builder()
                    .callerId(callerId)
                    .receiverId(List.of(signalingAction.getReceiverId()))
                    .callType(signalingAction.getCallType())
                    .expireTime(signalingAction.getExpireTime())
                    .build();
        }

        /**
         * 创建通话接受请求
         */
        public static CallingAcceptRequest createAcceptRequest(Long callerId, WSCallSignalingAction signalingAction) {
            return CallingAcceptRequest.builder()
                    .callerId(signalingAction.getReceiverId())
                    .receiverId(callerId)
                    .sessionId(signalingAction.getSessionId())
                    .sessionType(signalingAction.getSessionType())
                    .type(signalingAction.getType())
                    .build();
        }

        /**
         * 创建通话拒绝请求
         */
        public static CallingRejectRequest createRejectRequest(Long callerId, WSCallSignalingAction signalingAction) {
            return CallingRejectRequest.builder()
                    .callerId(signalingAction.getReceiverId())
                    .receiverId(callerId)
                    .sessionId(signalingAction.getSessionId())
                    .rejectReason(signalingAction.getRejectReason())
                    .build();
        }

        /**
         * 创建通话取消请求
         */
        public static CallingCancelRequest createCancelRequest(Long callerId, WSCallSignalingAction signalingAction) {
            return CallingCancelRequest.builder()
                    .callerId(signalingAction.getReceiverId())
                    .receiverId(callerId)
                    .sessionId(signalingAction.getSessionId())
                    .sessionType(signalingAction.getSessionType())
                    .expireTime(signalingAction.getExpireTime())
                    .cancelReason(signalingAction.getCancelReason())
                    .build();
        }
    }
}