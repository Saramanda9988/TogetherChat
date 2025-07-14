# TogetherChat 微服务架构依赖划分

## 架构概述

该项目已被重构为微服务架构，包含以下服务模块：

### 1. Common (公共模块)
- **职责**: 共享的实体类、工具类、常量、基础配置
- **主要依赖**:
  - Spring Boot Starter
  - MyBatis Plus
  - HuTool 工具包
  - Jackson JSON处理
  - JWT认证
  - 验证框架
  - AOP支持

### 2. Gateway (API网关)
- **职责**: 路由转发、负载均衡、认证授权、限流熔断
- **端口**: 8080 (建议)
- **主要依赖**:
  - Spring Cloud Gateway
  - Spring Boot Actuator
  - Redis (限流和会话管理)
  - JWT认证
  - Resilience4j (熔断器)
  - Common模块

### 3. User Server (用户服务)
- **职责**: 用户管理、认证授权、用户信息维护、文件上传
- **端口**: 8081 (建议)
- **主要依赖**:
  - Spring Boot Web
  - Spring Security
  - MySQL Driver
  - MyBatis Plus
  - JWT认证
  - MinIO/Aliyun OSS (文件存储)
  - Redis (缓存)
  - RabbitMQ (消息队列)
  - Common模块

### 4. Chat Server (聊天服务)
- **职责**: 消息发送、接收、存储，群组管理，WebSocket连接管理
- **端口**: 8082 (建议)
- **主要依赖**:
  - Spring Boot Web/WebFlux
  - WebSocket支持
  - Netty (WebSocket)
  - MySQL Driver
  - MyBatis Plus
  - Redis (缓存)
  - RabbitMQ (消息队列)
  - Redisson (分布式锁)
  - Common模块

### 5. AI Server (AI服务)
- **职责**: AI对话、智能回复、内容生成
- **端口**: 8083 (建议)
- **主要依赖**:
  - Spring Boot Web/WebFlux
  - Spring AI (OpenAI)
  - Spring AI Alibaba
  - MySQL Driver
  - MyBatis Plus
  - Redis (缓存)
  - RabbitMQ (消息队列)
  - Resilience4j (熔断器)
  - Common模块

### 6. WebRTC Server (音视频通话服务)
- **职责**: 音视频通话信令、会话管理、媒体服务器协调
- **端口**: 8084 (建议)
- **主要依赖**:
  - Spring Boot Web/WebFlux
  - WebSocket支持
  - Netty (信令服务)
  - MySQL Driver
  - MyBatis Plus
  - Redis (会话管理)
  - RabbitMQ (消息队列)
  - Redisson (分布式锁)
  - Common模块

## 服务间通信

### 1. 同步通信
- **方式**: RESTful API + Feign Client
- **场景**: 实时数据查询、用户认证验证

### 2. 异步通信
- **方式**: RabbitMQ 消息队列
- **场景**: 消息推送、事件通知、数据同步

### 3. 实时通信
- **方式**: WebSocket + Netty
- **场景**: 即时消息、音视频信令

## 数据存储

### 1. MySQL
- 用户信息、消息记录、群组信息、通话记录

### 2. Redis
- 用户会话、缓存数据、限流计数、分布式锁

### 3. 文件存储
- MinIO/Aliyun OSS: 用户头像、聊天图片、文件附件

## 部署建议

### 1. 构建顺序
```bash
# 1. 构建公共模块
cd common && mvn clean install

# 2. 构建各个微服务
cd gateway && mvn clean package
cd user-server && mvn clean package
cd chat-server && mvn clean package
cd ai-server && mvn clean package
cd webrtc-server && mvn clean package
```

### 2. 启动顺序
1. 基础设施: MySQL, Redis, RabbitMQ
2. User Server (用户服务)
3. Chat Server (聊天服务)
4. AI Server (AI服务)
5. WebRTC Server (音视频服务)
6. Gateway (API网关)

### 3. 环境配置
每个服务都需要配置：
- 数据库连接
- Redis连接
- RabbitMQ连接
- 服务注册与发现
- 配置中心

## 监控和治理

### 1. 服务监控
- Spring Boot Actuator
- Micrometer + Prometheus
- 自定义健康检查

### 2. 链路追踪
- Spring Cloud Sleuth
- Zipkin/Jaeger

### 3. 日志收集
- Logback + ELK Stack
- 集中式日志管理

## 安全考虑

### 1. 认证授权
- JWT Token认证
- Spring Security集成
- 网关统一认证

### 2. 服务间安全
- 内网通信
- 服务间认证
- API密钥管理

### 3. 数据安全
- 敏感数据加密
- SQL注入防护
- XSS防护

## 性能优化

### 1. 缓存策略
- Redis分布式缓存
- Caffeine本地缓存
- 多级缓存架构

### 2. 数据库优化
- 读写分离
- 分库分表
- 索引优化

### 3. 消息队列
- 异步处理
- 削峰填谷
- 事件驱动架构
