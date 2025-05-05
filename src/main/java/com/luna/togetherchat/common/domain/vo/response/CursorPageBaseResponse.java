package com.luna.togetherchat.common.domain.vo.response;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-19
 */
@Data
@Schema(description = "游标翻页返回")
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageBaseResponse<T> {

    @Schema(description = "游标（下次翻页带上这参数）")
    private Double cursor;

    @Schema(description = "是否最后一页")
    private Boolean isLast = Boolean.FALSE;

    @Schema(description = "数据列表")
    private List<T> list;

    /**
     * 这是一个工具类
     * 从代码原来的角度来说，我们是直接将 非T类 变成了 T 类 （比如 从Message 变成 chatMessage response）
    * */
    public static <T> CursorPageBaseResponse<T> init(CursorPageBaseResponse cursorPage, List<T> list) {
        CursorPageBaseResponse<T> cursorPageBaseResponse = new CursorPageBaseResponse<T>();
        cursorPageBaseResponse.setIsLast(cursorPage.getIsLast());
        cursorPageBaseResponse.setCursor(cursorPage.getCursor());

        cursorPageBaseResponse.setList(list);
        return cursorPageBaseResponse;
    }

    @JsonIgnore
    public Boolean isEmpty() {
        return CollectionUtil.isEmpty(list);
    }

    public static <T> CursorPageBaseResponse<T> empty() {
        CursorPageBaseResponse<T> cursorPageBaseResponse = new CursorPageBaseResponse<T>();
        cursorPageBaseResponse.setIsLast(true);
        cursorPageBaseResponse.setList(new ArrayList<T>());
        return cursorPageBaseResponse;
    }

}
