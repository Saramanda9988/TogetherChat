package com.luna.common.domain.vo.signalling;

import com.luna.common.domain.vo.WSBaseSignalling;
import lombok.Data;

@Data
public class WSEntry extends WSBaseSignalling {
    private Long userId; // 用户ID
    private String userName; // 用户名称
    private String userAvatar; // 用户头像
}
