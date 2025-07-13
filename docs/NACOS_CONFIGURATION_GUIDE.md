# TogetherChat 微服务架构配置指南

## 修复总结

### 1. 父子工程关系修复

**之前的问题：**
- 所有子模块都直接继承 `spring-boot-starter-parent`
- 子模块无法享受父工程的依赖管理
- 版本不一致

**修复后：**
- 所有子模块现在正确继承父工程 `TogetherChat`
- 父工程统一管理依赖版本
- 版本统一为 Spring Boot 3.3.11

### 2. 依赖管理优化

**父工程 (TogetherChat) 新增：**
```xml
<dependencyManagement>
    <dependencies>
        <!-- Spring Cloud Dependencies -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2023.0.4</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        
        <!-- Spring Cloud Alibaba Dependencies -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-dependencies</artifactId>
            <version>2023.0.3.2</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 3. Nacos 依赖集成

**所有微服务模块都添加了：**
- `spring-cloud-starter-alibaba-nacos-discovery` - 服务发现
- `spring-cloud-starter-alibaba-nacos-config` - 配置中心
- `spring-cloud-starter-loadbalancer` - 负载均衡

**Gateway 额外添加了：**
- `spring-cloud-starter-gateway` - API网关

### 4. 服务端口分配

| 服务 | 端口 | 说明 |
|------|------|------|
| gateway-server | 8080 | API网关 |
| chat-server | 8081 | 聊天服务 |
| user-server | 8082 | 用户服务 |
| room-server | 8083 | 房间服务 |
| webrtc-server | 8084 | WebRTC服务 |

### 5. Nacos 配置

**所有服务的 Nacos 配置：**
- Server地址: `localhost:8848`
- 命名空间: `dev`
- 分组: `DEFAULT_GROUP`
- 配置格式: `yml`

### 6. 网关路由配置

Gateway配置了以下路由：
- `/user/**` → user-server
- `/chat/**` → chat-server  
- `/room/**` → room-server
- `/webrtc/**` → webrtc-server

## 使用说明

### 1. 启动顺序
1. 先启动 Nacos Server (在nacos-server目录下)
2. 启动各个微服务
3. 最后启动 Gateway

### 2. Nacos 控制台
- 访问地址: http://localhost:8848/nacos
- 默认用户名/密码: nacos/nacos

### 3. 服务访问
- 通过Gateway访问: http://localhost:8080/{service-path}
- 直接访问服务: http://localhost:808{x}

## 注意事项

### 1. 依赖版本冲突
- 子模块中的某些依赖版本被父工程覆盖，这是正常的
- 建议移除子模块中的显式版本声明，使用父工程管理的版本

### 2. 配置文件优先级
- bootstrap.yml > application.yml
- Nacos远程配置 > 本地配置

### 3. 服务启动检查
启动后检查以下内容：
- Nacos控制台是否显示所有服务
- 服务健康检查是否正常
- Gateway路由是否正确转发

## 下一步建议

1. **配置外部化**: 将数据库、Redis等配置移到Nacos配置中心
2. **服务容错**: 添加Sentinel熔断器
3. **分布式事务**: 考虑使用Seata
4. **链路追踪**: 集成Sleuth或Skywalking
5. **API文档**: 统一使用Knife4j生成接口文档
