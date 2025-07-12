package com.luna.webrtcserver.call.controller;

import com.luna.webrtcserver.call.domain.entity.Session;
import com.luna.webrtcserver.call.domain.request.*;
import com.luna.webrtcserver.call.domain.response.SessionInfoResponse;
import com.luna.webrtcserver.call.domain.response.SessionResponse;
import com.luna.webrtcserver.call.service.CallService;
import com.luna.webrtcserver.common.domain.vo.response.ApiResult;
import com.luna.webrtcserver.common.utils.RequestHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/capi/call")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "CallController", description = "通话相关接口")
public class CallController {
    private final CallService callService;

    @PostMapping("/initiate")
    @Operation(summary = "发起通话", description = "发起语音或视频通话")
    public ApiResult<SessionInfoResponse> initiateCall(@RequestBody @Valid CallingRequest request) {
        Long userId = RequestHolder.get().getUserId();
        return ApiResult.success(callService.initiateCall(request, userId));
    }

    @PostMapping("/cancel")
    @Operation(summary = "结束通话", description = "挂断正在进行的通话")
    public ApiResult<Void> endCall(@RequestBody @Valid CallingCancelRequest request) {
        Long userId = RequestHolder.get().getUserId();
        callService.endCall(request, userId);
        return ApiResult.success();
    }

    @PostMapping("/history")
    @Operation(summary = "获取通话历史", description = "获取用户的通话历史记录")
    public ApiResult<List<SessionResponse>> getCallHistory() {
        Long userId = RequestHolder.get().getUserId();
        return ApiResult.success(callService.getCallHistory(userId));
    }
}