# WebSocket 信令格式文档

## 概述

本文档定义了 TogetherChat 应用中 WebRTC 通信的信令格式。所有的信令类型都在 `WSReqTypeEnum` 枚举中定义，用于标识不同类型的 WebSocket 请求。信令类都继承自 `WSBaseSignalling` 基类，包含通用的信令属性。

## 信令基类结构

### WSBaseSignalling 基类

所有信令类都继承自 `WSBaseSignalling`，包含以下通用字段：

- **type**: 信令类型码，对应 `WSReqTypeEnum` 中的类型值
- **sourceId**: 信令来源用户ID
- **targetId**: 信令目标用户ID
- **meetId**: 信令所属会议ID
- **key**: 信令的唯一标识符

信令在发送时，会使用WSMessage<>(后端为WSBaseResp<>)进行包装

```json
{
  "type": 4, // 会填写信令类型
  "data": {
    // 信令被存入data
  }
}
```

## 信令类型列表

### 4. 进入房间 (ENTER)

- **类型码**: 4
- **描述**: 用户进入聊天房间
- **用途**: 用户加入指定的聊天房间
- **对应类**: `WSEntry`
- **特有字段**:
  - `userId`: 用户ID
  - `userName`: 用户名称
  - `userAvatar`: 用户头像

### 5. 沟通媒体设备信息 (OFFER)

- **类型码**: 5
- **描述**: WebRTC Offer 信令
- **用途**: 发起音视频通话时发送媒体设备信息和会话描述
- **对应类**: `WSOffer`
- **特有字段**:
  - `description`: 媒体描述对象
    - `type`: 固定为 "offer"
    - `sdp`: 会话描述协议数据

### 6. 回复媒体设备信息 (ANSWER)

- **类型码**: 6
- **描述**: WebRTC Answer 信令
- **用途**: 响应音视频通话邀请，发送本地媒体设备信息
- **对应类**: `WSAnswer`
- **特有字段**:
  - `description`: 媒体描述对象
    - `type`: 固定为 "answer"
    - `sdp`: 会话描述协议数据

### 7. ICE 候选信息 (CANDIDATE)

- **类型码**: 7
- **描述**: ICE 候选信息，用于交换网络信息
- **用途**: WebRTC 连接建立过程中的网络地址协商
- **对应类**: `WSCandidate`
- **特有字段**:
  - `candidate`: ICE 候选对象
    - `candidate`: ICE 候选字符串
    - `sdpMid`: 媒体流标识符
    - `sdpMLineIndex`: 媒体行索引
    - `usernameFragment`: 用户名片段

### 8. 离开房间 (LEAVE)

- **类型码**: 8
- **描述**: 用户离开房间
- **用途**: 用户主动离开当前聊天房间
- **对应类**: `WSLeave`
- **特有字段**: 无（仅使用基类字段）

### 9. 结束房间 (END)

- **类型码**: 9
- **描述**: 结束房间
- **用途**: 房间管理员结束整个房间会话
- **说明**: 暂无对应的信令类实现

### 10. 加入房间 (JOIN)

- **类型码**: 10
- **描述**: 请求加入房间
- **用途**: 用户请求加入指定房间
- **对应类**: `WSJoin`
- **特有字段**: 无（仅使用基类字段）

### 11. 拒绝加入房间 (REJECT)

- **类型码**: 11
- **描述**: 拒绝加入房间请求
- **用途**: 房间管理员拒绝用户的加入请求
- **对应类**: `WSReject`
- **特有字段**: 无（仅使用基类字段）

### 12. 结束房间通话 (CANCEL)

- **类型码**: 12
- **描述**: 取消或结束房间通话
- **用途**: 结束当前房间的音视频通话会话
- **对应类**: `WSCancel`
- **特有字段**: 无（仅使用基类字段）

## 使用示例

### 进入房间信令示例

```json
{
  "type": 4,
  "sourceId": 123,
  "targetId": null,
  "meetId": 789,
  "key": "enter-room-key-123456789",
  "userId": 123,
  "userName": "张三",
  "userAvatar": "https://example.com/avatar/123.jpg"
}
```

### WebRTC Offer 信令示例

```json
{
  "type": 5,
  "sourceId": 123,
  "targetId": 456,
  "meetId": 789,
  "key": "offer-key-123456789",
  "description": {
    "type": "offer",
    "sdp": "v=0\r\no=- 123456789 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\na=group:BUNDLE 0\r\na=msid-semantic: WMS\r\nm=video 9 UDP/TLS/RTP/SAVPF 96\r\n..."
  }
}
```

### WebRTC Answer 信令示例

```json
{
  "type": 6,
  "sourceId": 456,
  "targetId": 123,
  "meetId": 789,
  "key": "answer-key-987654321",
  "description": {
    "type": "answer",
    "sdp": "v=0\r\no=- 987654321 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\na=group:BUNDLE 0\r\na=msid-semantic: WMS\r\nm=video 9 UDP/TLS/RTP/SAVPF 96\r\n..."
  }
}
```

### ICE 候选信息示例

```json
{
  "type": 7,
  "sourceId": 123,
  "targetId": 456,
  "meetId": 789,
  "key": "candidate-key-111222333",
  "candidate": {
    "candidate": "candidate:1 1 UDP 2113667327 192.168.1.100 54400 typ host",
    "sdpMid": "0",
    "sdpMLineIndex": "0",
    "usernameFragment": "abc123"
  }
}
```

### 加入房间信令示例

```json
{
  "type": 10,
  "sourceId": 123,
  "targetId": null,
  "meetId": 789,
  "key": "join-room-key-123456789"
}
```

### 离开房间信令示例

```json
{
  "type": 8,
  "sourceId": 123,
  "targetId": null,
  "meetId": 789,
  "key": "leave-room-key-123456789"
}
```

### 拒绝加入房间信令示例

```json
{
  "type": 11,
  "sourceId": 456,
  "targetId": 123,
  "meetId": 789,
  "key": "reject-join-key-987654321"
}
```

### 结束房间通话信令示例

```json
{
  "type": 12,
  "sourceId": 123,
  "targetId": null,
  "meetId": 789,
  "key": "cancel-call-key-123456789"
}
```

## 信令处理流程

### 用户加入房间流程

1. 用户发送 `JOIN` (10) 信令请求其他用户加入房间
2. 被邀请用户可以发送 `REJECT` (11) 信令拒绝请求
3. 用户成功加入后发送 `ENTER` (4) 信令进入房间，随后准备进行OFFER信令交换
4. 用户离开时发送 `LEAVE` (8) 信令

### 音视频通话流程

1. 发起方发送 `OFFER` (5) 信令，包含媒体能力信息
2. 接收方发送 `ANSWER` (6) 信令，响应媒体能力信息
3. 双方交换 `CANDIDATE` (7) 信令建立网络连接
4. 通话结束时发送 `CANCEL` (12) 信令

## 注意事项

1. 所有信令都必须包含完整的基类字段（type、sourceId、targetId、meetId、key）
2. WebRTC 相关信令需要按照标准流程发送（OFFER -> ANSWER -> CANDIDATE），ENTRY/REJECT会在OFFER发送之前发送，获得ENTRY信令紧接着进行标准流程发送
3. 房间管理权限需要在服务端验证
4. 信令的 key 字段用于标识唯一的信令，使用 UUID
5. targetId 在广播信令中可以为 null（如进入房间、离开房间等）
6. 所有信令都应该经过适当的数据验证和错误处理

## 版本信息

- 文档版本: 2.0
- 最后更新: 2025-07-04
- 对应代码版本: WSReqTypeEnum.java, WSBaseSignalling.java 及相关信令类
