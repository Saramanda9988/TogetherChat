package com.luna.userserver.websocket.monitor;

import com.luna.userserver.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * WebSocket连接状态监控组件
 */
@Slf4j
@Component
public class WebSocketMonitor {
    
    @Autowired
    private WebSocketService webSocketService;
    
    /**
     * 定时记录WebSocket连接状态
     */
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void logConnectionStatus() {
        int onlineUsers = webSocketService.getOnlineUserCount();
        int activeConnections = webSocketService.getActiveConnectionCount();
        
        log.info("WebSocket连接状态 - 在线用户数: {}, 活跃连接数: {}", 
                onlineUsers, activeConnections);
    }
}
