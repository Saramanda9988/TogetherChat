package com.luna.togetherchat.chat.domain.entity.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author 苍镜月
 * @version 1.0
 * @implNote 图片消息传输
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)  // 添加此注解
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SoundMessage extends FileMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "时长（秒）")
    @NotNull
    private Integer second;
}
