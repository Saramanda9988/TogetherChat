package com.luna.togetherchat.call.controller;

import com.luna.togetherchat.call.domain.request.*;
import com.luna.togetherchat.call.domain.response.CallHistoryResponse;
import com.luna.togetherchat.call.service.CallService;
import com.luna.togetherchat.common.domain.vo.response.ApiResult;
import com.luna.togetherchat.common.utils.RequestHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/capi/call")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "CallController", description = "通话相关接口")
public class CallController {
    private final CallService callService;

    @PostMapping("/initiate")
    @Operation(summary = "发起通话(备用)", description = "发起语音或视频通话")
    public ApiResult<Void> initiateCall(@RequestBody @Valid CallingRequest request) {
        Long userId = RequestHolder.get().getUserId();
        callService.initiateCall(request, userId);
        return ApiResult.success();
    }

    @PostMapping("/accept")
    @Operation(summary = "接受通话(备用)", description = "接受来电")
    public ApiResult<Void> acceptCall(@RequestBody @Valid CallingAcceptRequest request) {
        Long userId = RequestHolder.get().getUserId();
        callService.acceptCall(request, userId);
        return ApiResult.success();
    }

    @PostMapping("/reject")
    @Operation(summary = "拒绝通话(备用)", description = "拒绝来电")
    public ApiResult<Void> rejectCall(@RequestBody @Valid CallingRejectRequest request) {
        Long userId = RequestHolder.get().getUserId();
        callService.rejectCall(request, userId);
        return ApiResult.success();
    }

    @PostMapping("/cancel")
    @Operation(summary = "结束通话(备用)", description = "挂断正在进行的通话")
    public ApiResult<Void> endCall(@RequestBody @Valid CallingCancelRequest request) {
        Long userId = RequestHolder.get().getUserId();
        callService.endCall(request, userId);
        return ApiResult.success();
    }
}