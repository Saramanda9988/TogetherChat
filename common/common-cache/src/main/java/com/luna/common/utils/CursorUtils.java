package com.luna.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import com.luna.common.domain.vo.request.CursorPageBaseRequest;
import com.luna.common.domain.vo.response.CursorPageBaseResponse;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 游标分页工具类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-28
 */
public class CursorUtils {

    public static <T> CursorPageBaseResponse<Pair<T, Double>> getCursorPageByRedis
            (CursorPageBaseRequest cursorPageBaseRequest, String redisKey, Function<String, T> typeConvert) {

        Set<ZSetOperations.TypedTuple<String>> typedTuples;

        if (cursorPageBaseRequest.getCursor() == null) {//第一次
            typedTuples = RedisUtils.zReverseRangeWithScores(redisKey, cursorPageBaseRequest.getPageSize());
        } else {
            typedTuples = RedisUtils.zReverseRangeByScoreWithScores(redisKey, Double.parseDouble(String.valueOf(cursorPageBaseRequest.getCursor())), cursorPageBaseRequest.getPageSize());
        }

        List<Pair<T, Double>> result = typedTuples
                .stream()
                .map(t -> Pair.of(typeConvert.apply(t.getValue()), t.getScore()))
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .collect(Collectors.toList());

        String cursor = String.valueOf(Optional.ofNullable(CollectionUtil.getLast(result))
                .map(Pair::getValue)
                .orElse(null));

        Boolean isLast = result.size() != cursorPageBaseRequest.getPageSize();

        return new CursorPageBaseResponse<>(Double.parseDouble(cursor), isLast, result);
    }

    private static String toCursor(Object o) {
        if (o instanceof Date) {
            return String.valueOf(((Date) o).getTime());
        } else {
            return o.toString();
        }
    }

    private static Object parseCursor(String cursor, Class<?> cursorClass) {
        if (Date.class.isAssignableFrom(cursorClass)) {
            return new Date(Long.parseLong(cursor));
        } else {
            return cursor;
        }
    }
}
