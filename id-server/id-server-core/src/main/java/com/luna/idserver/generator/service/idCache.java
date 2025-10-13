package com.luna.idserver.generator.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <p>
 *  id缓存器
 * </p>
 *
 * @author LunaRain_079
 * @since 2025-05-05
 */
@Component
@RequiredArgsConstructor
public class idCache {
    @PostConstruct
    private void init() {

    }

    public Long getNextSyncId(Long groupId) {
        return 0L;
    }
}
