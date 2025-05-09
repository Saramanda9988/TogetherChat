package com.luna.togetherchat.common.constant;

/**
 * @author jxc modify on 2024/10/15
 */
public class RedisKey {
    private static final String BASE_KEY = "togetherchat:";

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

    public static String getKey(String key, Object... objects) {
        return BASE_KEY + String.format(key, objects);
    }
}
