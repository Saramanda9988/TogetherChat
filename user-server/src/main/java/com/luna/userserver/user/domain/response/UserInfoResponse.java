package com.luna.userserver.user.domain.response;

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

    @Schema(description = "1在线 0离线")
    private Integer activeStatus;
}
