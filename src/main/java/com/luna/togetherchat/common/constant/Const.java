package com.luna.togetherchat.common.constant;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Const {

    /** 
     * 用于给Jwt令牌签名校验的秘钥 
     */
    public static final String JWT_SIGN_KEY = "0e7335d7-704c";
    
    /** 
     * refresh_token cookie 名称 
     */
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    
    /** 
     * 存放用户信息的jwt key 
     */
    public static final String USER_ID_KEY = "userId";
    
    /** 
     * 存放用户名的jwt key 
     */
    public static final String USER_NAME_KEY = "userName";

    /**
     * access_token 过期时间 30分钟 (单位: 毫秒)
     */
    public static final Long ACCESS_TOKEN_EXPIRE_TIME = 60 * 30 * 1000L;
    
    /** 
     * refresh_token 过期时间 7天 (单位: 毫秒)
     */
    public static final Long REFRESH_TOKEN_EXPIRE_TIME = 60 * 24 * 7 * 1000L;
    
    /** 
     * token发行者 
     */
    public static final String ISS = "LunaRain";

    /**
     * 用于给Jwt令牌签名校验的秘钥
     */
    public static final SecretKey KEY = new SecretKeySpec(
            Arrays.copyOf(JWT_SIGN_KEY.getBytes(StandardCharsets.UTF_8), 64), "HmacSHA256");
}
