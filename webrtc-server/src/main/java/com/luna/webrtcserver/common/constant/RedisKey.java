package com.luna.webrtcserver.common.constant;

/**
 * @author jxc modify on 2024/10/15
 */
public class RedisKey {
    private static final String BASE_KEY = "webrtcserver:";

    /** refresh token */
    public static final String REFRESH_TOKEN = "refresh_token:%s";

    /** 在线用户列表 */
    public static final String ONLINE_UID_ZET = "online";
    /** 离线用户列表 */
    public static final String OFFLINE_UID_ZET = "offline";

    /** 用户信息 */
    public static final String USER_INFO_STRING = "userInfo:uid_%d";

    /** 用户角色 */
    public static final String USER_ROLE_STRING = "userRole:rid_%d";

    /** 房间详情 */
    public static final String ROOM_INFO_STRING = "roomInfo:roomId_%d";

    /** 群组详情 */
    public static final String GROUP_INFO_STRING = "groupInfo:roomId_%d";

    /** 群组详情 */
    public static final String GROUP_FRIEND_STRING = "groupFriend:roomId_%d";

    /** 用户token存放 */
    public static final String USER_TOKEN_STRING = "userToken:uid_%d";

    /** 用户的信息更新时间 */
    public static final String USER_MODIFY_STRING = "userModify:uid_%d";

    /** 用户的信息汇总 */
    public static final String USER_SUMMARY_STRING = "userSummary:uid_%d";

    /** 用户当前通话会话 */
    public static final String USER_CALL_SESSION = "userCallSession:uid_%d";

    /** 通话会话参与者 */
    public static final String CALL_SESSION_MEMBERS = "callSessionMembers:sid_%d";

    /** 通话会话锁 */
    public static final String CALL_SESSION_LOCK = "callSessionLock:sid_%d:uid_%d";

    public static String getKey(String key, Object... objects) {
        return BASE_KEY + String.format(key, objects);
    }

    /**
     * 获取用户当前通话会话的键
     */
    public static String getUserCallSessionKey(Long userId) {
        return String.format(USER_CALL_SESSION, userId);
    }

    /**
     * 获取通话会话参与者的键
     */
    public static String getCallSessionMembersKey(Long sessionId) {
        return String.format(CALL_SESSION_MEMBERS, sessionId);
    }

    /**
     * 获取通话会话锁的键
     */
    public static String getCallSessionLockKey(Long sessionId, Long userId) {
        return String.format(CALL_SESSION_LOCK, sessionId, userId);
    }
}
