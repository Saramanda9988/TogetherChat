package com.luna.togetherchat.user.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginInfoResponse {
    @Schema(description = "用户的uid，全局唯一")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "1在线 2离线")
    private Integer activeStatus;

    @Schema(description = "最后一次上下线时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "access_token 注意：后端返回的 token 不携带Bearer前缀")
    private String accessToken;
}
