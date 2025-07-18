package com.luna.common.domain.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: ws的基本返回信息体
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WSBaseResp<T> {

    private Integer type;
    private T data;
}
