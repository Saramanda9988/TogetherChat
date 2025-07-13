# TogetherChat 配置说明

## 配置文件设置

为了保护敏感信息，所有的 `application.properties` 文件都被添加到了 `.gitignore` 中，不会被提交到 GitHub。

### 设置步骤

1. 在每个微服务模块中，将 `application.properties.template` 文件复制并重命名为 `application.properties`
2. 根据你的实际环境修改配置文件中的参数

### 各模块配置文件位置

- **chat-server**: `chat-server/src/main/resources/application.properties`
- **user-server**: `user-server/src/main/resources/application.properties`
- **gateway-server**: `gateway-server/src/main/resources/application.properties`
- **room-server**: `room-server/src/main/resources/application.properties`
- **webrtc-server**: `webrtc-server/src/main/resources/application.properties`

### 主要配置项说明

#### 数据库配置

- `spring.datasource.url`: 数据库连接URL
- `spring.datasource.username`: 数据库用户名
- `spring.datasource.password`: 数据库密码

#### Redis配置（如果使用）

- `spring.redis.host`: Redis服务器地址
- `spring.redis.port`: Redis端口
- `spring.redis.password`: Redis密码

#### JWT配置（user-server）

- `jwt.secret`: JWT密钥
- `jwt.expiration`: JWT过期时间

#### WebRTC配置（webrtc-server）

- `webrtc.stun.servers`: STUN服务器
- `webrtc.turn.servers`: TURN服务器
- `webrtc.turn.username`: TURN用户名
- `webrtc.turn.password`: TURN密码

### 注意事项

1. 请不要将包含敏感信息的 `application.properties` 文件提交到版本控制系统
2. 确保每个团队成员都有自己的配置文件副本
3. 如果需要添加新的配置项，请同时更新对应的 `.template` 文件
4. 生产环境建议使用环境变量或外部配置管理系统来管理敏感配置
