package com.luna.togetherchat.common.domain.vo.request;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 基础分页请求类
 */
@Data
@Schema(description = "基础分页请求")
public class PageBaseRequest {
    
    @Schema(description = "页码", defaultValue = "1")
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNo = 1;
    
    @Schema(description = "每页大小", defaultValue = "10")
    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 100, message = "每页条数不能大于100")
    private Integer pageSize = 10;
    
    /**
     * 获取MyBatis-Plus分页对象
     */
    public <T> Page<T> plusPage() {
        return new Page<>(pageNo, pageSize);
    }
}
