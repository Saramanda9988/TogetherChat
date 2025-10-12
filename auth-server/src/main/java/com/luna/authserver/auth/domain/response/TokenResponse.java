package com.luna.authserver.auth.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    /**
     * 访问token
     */
    private String accessToken;
    
    /**
     * 刷新token
     */
    private String refreshToken;
}