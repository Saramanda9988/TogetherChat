package com.luna.togetherchat.websocket.domain.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 通话同意的推送类
 * Author: Luna
 * Date: 2025-05-10
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSCallAgree extends WSCallSignalingAction {

}
