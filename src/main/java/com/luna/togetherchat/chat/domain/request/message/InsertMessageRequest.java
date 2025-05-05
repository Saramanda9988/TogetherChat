package com.luna.togetherchat.chat.domain.request.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "插入消息请求")
public class InsertMessageRequest {
    
    @Schema(description = "消息内容")
    private String content;
    
    @Schema(description = "房间ID")
    @NotNull(message = "房间ID不能为空")
    private Long roomId;
    
    @Schema(description = "消息类型")
    @NotNull(message = "消息类型不能为空")
    private Integer messageType;
    
    @Schema(description = "消息体")
    private Object body;
    
    @Schema(description = "角色ID")
    private Long roleId;
    
    @Schema(description = "头像ID")
    private Long avatarId;
    
    @Schema(description = "插入位置前面的消息ID")
    private Long beforeMessageId;
    
    @Schema(description = "插入位置后面的消息ID")
    private Long afterMessageId;
    
    /**
     * 将InsertMessageRequest转换为ChatMessageRequest
     * 遵循DDD原则，转换逻辑应当封装在领域对象内部
     * 
     * @return ChatMessageRequest对象
     */
    public ChatMessageRequest toChatMessageRequest() {
        return ChatMessageRequest.builder()
                .roomId(this.roomId)
                .messageType(this.messageType)
                .content(this.content)
                .body(this.body)
                .roleId(this.roleId)
                .avatarId(this.avatarId)
                .build();
    }
}
