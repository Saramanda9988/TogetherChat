# 项目通用开发规范

## 1. 微服务模块结构规范

### 1.1 模块分层结构
所有微服务模块都应采用`api` + `core`的分层结构：
- **api模块**：定义对外暴露的接口和服务契约，包括DTO,接口定义,dubbo服务接口
- **core模块**：实现具体业务逻辑

## 2. 代码结构规范

### 2.1 包结构组织
每个微服务模块应按照功能划分包结构：
```
com.luna.[service-name]
├── [domain-name]           # 领域模块
│   ├── controller          # 控制器层
│   ├── service             # 服务层
│   ├── dao                 # 数据访问对象层
│   ├── mapper              # MyBatis映射层
│   └── domain              # 领域对象
│       ├── entity          # 实体类（与数据库表对应）
│       ├── request         # 请求对象（用于接收前端请求）
│       └── response        # 响应对象（用于返回给前端）
└── ...
```

## 3. 数据传输规范

### 3.1 DTO设计原则
- **请求DTO**：用于接收前端请求参数，应包含必要的校验注解
- **响应DTO**：用于返回给前端的数据结构，只包含需要的信息
- **服务DTO**：用于微服务间通信的数据结构

### 3.2 校验规范
- 使用Jakarta Validation注解进行参数校验
- 在请求DTO中添加合适的校验注解（如[@NotBlank](file://D:\Java_project\TogetherDev\TogetherChat\user-server\user-server-core\src\main\java\com\luna\userserver\user\domain\request\UserLoginRequest.java#L15-L15)、[@Size](file://D:\Java_project\TogetherDev\TogetherChat\user-server\user-server-core\src\main\java\com\luna\userserver\user\domain\request\UserRegisterRequest.java#L16-L16)等）
- 在Controller方法参数上添加[@Valid](file://D:\Java_project\TogetherDev\TogetherChat\common\common-core\src\main\java\com\luna\common\utils\JwtUtils.java#L119-L119)注解启用校验

### 3.3 枚举规范
- 使用枚举类定义常量值（如状态码、类型等）
- 数据库中类型字段使用Integer存储，建议使用枚举类管理
- 枚举类应包含type和description字段，使用@Getter和@AllArgsConstructor注解

## 4. 接口设计规范

### 4.1 RESTful API规范
- 使用标准HTTP方法：GET（查询）、POST（创建）、PUT（更新）、DELETE（删除）
- 使用统一的响应格式：[ApiResult](file://D:\Java_project\TogetherDev\TogetherChat\common\common-core\src\main\java\com\luna\common\domain\vo\response\ApiResult.java#L1-L51)<T>
- 控制器类使用[@RestController](file://D:\Java_project\TogetherDev\TogetherChat\user-server\user-server-core\src\main\java\com\luna\userserver\user\controller\UserController.java#L17-L17)注解
- 使用[@RequestMapping](file://D:\Java_project\TogetherDev\TogetherChat\user-server\user-server-core\src\main\java\com\luna\userserver\user\controller\UserController.java#L19-L19)指定基础路径

### 4.2 微服务间通信规范
- 使用Dubbo进行微服务间通信
- 定义服务接口时遵循面向接口编程原则
- 在api模块中定义接口和服务DTO
- 在core模块中实现具体服务逻辑

## 5. 数据库操作规范

### 5.1 ORM框架使用
- 使用MyBatis-Plus作为ORM框架
- 实体类使用MyBatis-Plus注解（[@TableName](file://D:\Java_project\TogetherDev\TogetherChat\user-server\user-server-core\src\main\java\com\luna\userserver\user\domain\entity\User.java#L31-L31)、[@TableId](file://D:\Java_project\TogetherDev\TogetherChat\user-server\user-server-core\src\main\java\com\luna\userserver\user\domain\entity\User.java#L36-L36)、[@TableField](file://D:\Java_project\TogetherDev\TogetherChat\user-server\user-server-core\src\main\java\com\luna\userserver\user\domain\entity\User.java#L40-L40)等）
- DAO层继承ServiceImpl类，使用LambdaQuery进行查询

### 5.2 事务管理
- 在服务层方法上使用[@Transactional](file://D:\Java_project\TogetherDev\TogetherChat\user-server\user-server-core\src\main\java\com\luna\userserver\user\service\UserService.java#L32-L32)注解管理事务
- 合理控制事务范围，避免长时间占用数据库连接

## 6. 技术组件使用规范

### 6.1 缓存操作
- 项目中操作Redis应使用统一的[RedisUtils](file://D:\Java_project\TogetherDev\TogetherChat\common\common-cache\src\main\java\com\luna\common\utils\RedisUtils.java#L1-L216)工具类
- 避免直接调用Redisson或其他Redis客户端API

### 6.2 日志记录
- 使用SLF4J进行日志记录
- 在类上添加[@Slf4j](file://D:\Java_project\TogetherDev\TogetherChat\user-server\user-server-core\src\main\java\com\luna\userserver\UserServerApplication.java#L25-L25)注解自动生成logger对象
- 合理使用不同级别的日志（trace、debug、info、warn、error）

### 6.3 配置管理
- 使用Nacos进行配置管理
- 通过application.yml配置服务信息
- 使用占位符方式配置环境相关参数

## 7. 代码质量规范

### 7.1 代码注解与文档
- 使用Lombok注解减少样板代码（[@Data](file://D:\Java_project\TogetherDev\TogetherChat\user-server\user-server-api\src\main\java\com\luna\userserver\api\dto\UserDTO.java#L13-L13)、[@Builder](file://D:\Java_project\TogetherDev\TogetherChat\user-server\user-server-api\src\main\java\com\luna\userserver\api\dto\UserDTO.java#L12-L12)、[@AllArgsConstructor](file://D:\Java_project\TogetherDev\TogetherChat\user-server\user-server-api\src\main\java\com\luna\userserver\api\dto\UserDTO.java#L11-L11)、[@NoArgsConstructor](file://D:\Java_project\TogetherDev\TogetherChat\user-server\user-server-api\src\main\java\com\luna\userserver\api\dto\UserDTO.java#L10-L10)等）
- 使用Swagger注解（[@Schema](file://D:\Java_project\TogetherDev\TogetherChat\user-server\user-server-core\src\main\java\com\luna\userserver\user\domain\entity\User.java#L33-L33)、[@Operation](file://D:\Java_project\TogetherDev\TogetherChat\user-server\user-server-core\src\main\java\com\luna\userserver\user\controller\UserController.java#L24-L24)等）生成API文档

### 7.2 异常处理
- 有业务问题抛出抛出业务异常（[@BusinessException](file://D:\Java_project\TogetherDev\TogetherChat\common\common-core\src\main\java\com\luna\common\exception\BusinessException.java)）

### 7.3 依赖注入
- 使用构造函数注入（通过[@RequiredArgsConstructor](file://D:\Java_project\TogetherDev\TogetherChat\user-server\user-server-core\src\main\java\com\luna\userserver\user\controller\UserController.java#L21-L21)）
- 避免使用字段注入
