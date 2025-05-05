package com.luna.togetherchat.chat.domain.entity.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author 苍镜月
 * @version 1.0
 * @implNote 文件传输基类
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)  // 添加此注解
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileMessage implements Serializable {
    @Schema(description = "大小（字节）")
    @NotNull
    private Long size;

    @Schema(description = "下载地址")
    @NotBlank
    private String url;

    @Schema(description = "文件名（带后缀）")
    @NotBlank
    private String fileName;

    private static final long serialVersionUID = 1L;
}
