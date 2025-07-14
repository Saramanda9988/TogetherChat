package com.luna.gatewayserver.controller;

import com.luna.common.domain.vo.WebSocketEndpointVO;
import com.luna.common.domain.vo.WebSocketHealthVO;
import com.luna.common.domain.vo.WebSocketInstanceVO;
import com.luna.common.domain.vo.response.ApiResult;
import com.luna.common.enums.WsDiscoveryErrorEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * WebSocket服务发现控制器
 * 统一为前端提供WebSocket服务地址发现功能
 *
 * 设计原则：
 * 1. 所有前端请求统一通过Gateway入口
 * 2. Gateway负责服务发现和负载均衡
 * 3. WebSocket Server专注于WebSocket连接处理
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/capi/websocket")
public class WebSocketDiscoveryController {

    private final DiscoveryClient discoveryClient;

    /**
     * 获取可用的WebSocket服务端点
     * @return WebSocket服务地址信息
     */
    @GetMapping("/endpoint")
    public ApiResult<WebSocketEndpointVO> getWebSocketEndpoint() {
        try {
            // 从Nacos获取websocket-server服务实例列表
            List<ServiceInstance> instances = discoveryClient.getInstances("websocket-server");

            if (instances.isEmpty()) {
                log.warn("Gateway: 没有找到可用的websocket-server服务实例");
                return ApiResult.fail(WsDiscoveryErrorEnum.NO_SERVER_FOUND);
            }

            // 使用负载均衡策略选择一个实例
            ServiceInstance selectedInstance = selectInstance(instances);

            // 构建WebSocket连接URL
            String host = selectedInstance.getHost();
            int port = selectedInstance.getPort();

            // WebSocket URL格式: ws://host:port/
            String wsUrl = String.format("ws://%s:%d/", host, port);

            WebSocketEndpointVO endpoint = WebSocketEndpointVO.builder()
                    .host(host)
                    .port(port)
                    .wsUrl(wsUrl)
                    .serviceId(selectedInstance.getServiceId())
                    .instanceId(selectedInstance.getInstanceId())
                    .build();

            log.info("Gateway为前端分配WebSocket服务端点: {}", wsUrl);
            return ApiResult.success(endpoint);

        } catch (Exception e) {
            log.error("Gateway获取WebSocket服务端点失败", e);
            return ApiResult.fail(WsDiscoveryErrorEnum.SERVER_UNAVAILABLE);
        }
    }

    /**
     * 获取WebSocket服务健康状态
     * @return WebSocket服务健康信息
     */
    @GetMapping("/health")
    public ApiResult<WebSocketHealthVO> getWebSocketHealth() {
        try {
            List<ServiceInstance> instances = discoveryClient.getInstances("websocket-server");

            // 计算健康实例数
            long healthyInstances = instances.stream()
                    .filter(this::isInstanceHealthy)
                    .count();

            WebSocketHealthVO health = WebSocketHealthVO.builder()
                    .status(instances.isEmpty() ? "DOWN" : "UP")
                    .totalInstances(instances.size())
                    .healthyInstances((int)healthyInstances)
                    .discoveredAt(System.currentTimeMillis())
                    .build();

            log.info("WebSocket服务健康状态: 总实例={}, 健康实例={}", instances.size(), healthyInstances);
            return ApiResult.success(health);

        } catch (Exception e) {
            log.error("Gateway获取WebSocket服务健康状态失败", e);
            return ApiResult.fail(WsDiscoveryErrorEnum.FAIL_TO_GET_HEALTH);
        }
    }

    /**
     * 获取所有WebSocket服务实例列表
     * @return 服务实例列表
     */
    @GetMapping("/instances")
    public ApiResult<List<WebSocketInstanceVO>> getWebSocketInstances() {
        try {
            List<ServiceInstance> instances = discoveryClient.getInstances("websocket-server");

            List<WebSocketInstanceVO> instanceList = instances.stream()
                    .map(this::convertToInstanceVO)
                    .toList();

            log.info("获取WebSocket服务实例列表: {} 个实例", instanceList.size());
            return ApiResult.success(instanceList);

        } catch (Exception e) {
            log.error("Gateway获取WebSocket服务实例列表失败", e);
            return ApiResult.fail(WsDiscoveryErrorEnum.FAIL_TO_GET_INSTANCE_LIST);
        }
    }

    /**
     * 选择一个WebSocket服务实例
     * 支持多种负载均衡策略
     */
    private ServiceInstance selectInstance(List<ServiceInstance> instances) {
        // 过滤健康实例
        List<ServiceInstance> healthyInstances = instances.stream()
                .filter(this::isInstanceHealthy)
                .toList();

        if (healthyInstances.isEmpty()) {
            log.warn("没有健康的WebSocket服务实例，使用所有实例");
            healthyInstances = instances;
        }

        // 使用随机选择策略（可以扩展为其他策略）
        Random random = new Random();
        ServiceInstance selected = healthyInstances.get(random.nextInt(healthyInstances.size()));
        log.debug("选择WebSocket实例: {}:{}", selected.getHost(), selected.getPort());
        return selected;
    }

    /**
     * 检查服务实例是否健康
     * @param instance 服务实例
     * @return 是否健康
     */
    private boolean isInstanceHealthy(ServiceInstance instance) {
        // 这里可以添加更复杂的健康检查逻辑
        // 例如：检查实例的健康检查端点、检查实例元数据等
        return instance.getMetadata().getOrDefault("healthy", "true").equals("true");
    }

    /**
     * 转换服务实例为VO对象
     * @param instance 服务实例
     * @return 实例VO
     */
    private WebSocketInstanceVO convertToInstanceVO(ServiceInstance instance) {
        return WebSocketInstanceVO.builder()
                .instanceId(instance.getInstanceId())
                .serviceId(instance.getServiceId())
                .host(instance.getHost())
                .port(instance.getPort())
                .secure(instance.isSecure())
                .uri(instance.getUri().toString())
                .metadata(instance.getMetadata())
                .healthy(isInstanceHealthy(instance))
                .build();
    }
}