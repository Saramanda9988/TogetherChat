package com.luna.togetherchat.user.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {
    @NotNull
    @Schema(description = "用户的uid，全局唯一")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "头像的url")
    private String avatar;

    @Schema(description = "1在线 2离线")
    private Byte activeStatus;

    @Schema(description = "最后一次上下线时间")
    private LocalDateTime lastLoginTime;
}
