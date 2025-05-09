package com.luna.togetherchat.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.luna.togetherchat.common.constant.Const;
import com.luna.togetherchat.common.domain.dto.RequestInfo;
import com.luna.togetherchat.common.enums.CommonErrorEnum;
import com.luna.togetherchat.common.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * Description: jwt的token生成与解析
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-04-03
 */
@Slf4j
@Component
public class JwtUtils {

    /**
     * token秘钥，请勿泄露，请勿随便修改
     */
    @Value("${tuanchat.jwt.secret}")
    private static String secret;
    // token前缀
    public static final String BEARER = "Bearer ";

    private static final String UID_CLAIM = "uid";
    private static final String CREATE_TIME = "createTime";

    /**
     * JWT生成Token.<br/>
     * <p>
     * JWT构成: header, payload, signature
     */
    public static String createToken(Long uid) {
        // build token
        String token = JWT.create()
                .withClaim(UID_CLAIM, uid) // 只存一个uid信息，其他的自己去redis查
                .withClaim(CREATE_TIME, new Date())
                .sign(Algorithm.HMAC256(secret)); // signature
        return token;
    }

    /**
     * 解密Token
     *
     * @param token
     * @return
     */
    public Map<String, Claim> verifyToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaims();
        } catch (Exception e) {
            log.error("decode error,token:{}", token, e);
        }
        return null;
    }


    /**
     * 根据Token获取uid
     *
     * @param token
     * @return uid
     */
    public Long getUidOrNull(String token) {
        return Optional.ofNullable(verifyToken(token))
                .map(map -> map.get(UID_CLAIM))
                .map(Claim::asLong)
                .orElse(null);
    }

    /**
     * 生成用户 Token
     *
     * @param user 用户信息
     * @return 用户访问 Token(不携带Bearer)
     */
    public static String generateAccessToken(RequestInfo user) {
        Map<String, Object> userInfoMap = Map.of(Const.USER_ID_KEY, user.getUserId(),
                Const.USER_NAME_KEY, user.getUsername()
        );
        // 生成jwt token
        return Jwts.builder()
                .addClaims(userInfoMap)
                .setIssuedAt(new Date())
                .setIssuer(Const.ISS)
                .setExpiration(new Date(System.currentTimeMillis() + Const.ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(Const.KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析用户 Token
     *
     * @param jwtToken 用户访问 Token
     * @return 用户信息
     */
    public static RequestInfo parseJwtToken(String jwtToken) {
        if (org.springframework.util.StringUtils.hasText(jwtToken)) {
            String actualJwtToken = jwtToken.replace(BEARER, "");
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(Const.KEY)
                        .build()
                        .parseClaimsJws(actualJwtToken)
                        .getBody();

                return RequestInfo.builder()
                        .userId(claims.get(Const.USER_ID_KEY, Long.class))
                        .username(claims.get(Const.USER_NAME_KEY, String.class))
                        .build();
            } catch (Exception ex) {
                log.warn("JWT Token解析失败，请检查", ex);
                throw new UnauthorizedException(CommonErrorEnum.PARAM_VALID);
            }
        }
        return null;
    }

}
