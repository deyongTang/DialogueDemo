# 企业对话系统 Demo 设计文档

## 1. 目标

- 搭建一个简单的企业级对话系统 Demo，帮助学习业务流程与基础架构。
- 使用 Java + Spring Boot 技术栈，实现可运行的 REST 接口。
- 重点演示业务模块协作与流程，不追求复杂的对话算法。

## 2. 系统架构

```mermaid
graph TD
  A[渠道入口
  (Web/小程序/客服)] -->|HTTP/JSON| B[API 网关]
  B --> C[Dialogue Service
  (Spring Boot)]
  C --> D[会话状态管理
  (In-Memory Store)]
  C --> E[业务知识库
  (配置驱动)]
  C --> F[日志与监控]
  F --> G[可观测平台]
```

**说明**
- 渠道入口发送请求至 API 网关，可直接用 Nginx 或轻量级网关模拟。
- Dialogue Service 是本项目核心：负责对话流控制、策略选择、回复生成。
- 状态管理示例使用内存 Map，真实场景可替换为 Redis。
- 业务知识库使用配置脚本 (YAML/JSON) 维护简单的对话节点。
- 日志与监控在 Demo 中以 Console 日志、简单指标暴露的形式呈现。

## 3. 业务流程

```mermaid
flowchart LR
  S[开始
  用户发送消息] --> R[API 接收
  校验参数]
  R --> L{是否存在会话?}
  L -- 否 --> N[创建会话上下文]
  L -- 是 --> C[加载上下文]
  N --> P[匹配业务意图]
  C --> P
  P --> D{匹配到配置?}
  D -- 否 --> U[返回兜底回复]
  D -- 是 --> A[执行业务动作
  (查询脚本/调接口)]
  A --> T[生成回复 + 更新上下文]
  T --> E[输出响应并记录日志]
  U --> E
  E --> X[结束]
```

**流程说明**
1. 渠道层将用户消息通过 POST 请求发送到 `/api/dialogue/message`。
2. Controller 层完成参数校验与会话 ID 识别。
3. Service 层根据会话状态决定是否初始化上下文。
4. 意图匹配由配置脚本决定，Demo 中通过关键字匹配。
5. 匹配成功后触发预设的业务动作（例如查询 FAQ、返回订单状态）。
6. 生成回复、更新上下文并记录日志；若匹配失败则返回兜底回复。

## 4. 模块划分

| 模块 | 功能 |
| ---- | ---- |
| `DialogueController` | 对外 REST 接口，负责请求参数校验和响应包装。 |
| `DialogueService` | 核心业务逻辑：上下文管理、意图匹配、回复生成。 |
| `ConversationContextStore` | 简易的会话状态存储，封装 Map 操作。 |
| `IntentConfigLoader` | 从配置文件加载意图定义，供 Service 查询。 |
| `IntentDefinition` | 数据模型，描述触发关键字、响应模板和业务动作。 |

## 5. 数据模型

```text
IntentDefinition
├─ id: String
├─ keywords: List<String>
├─ replyTemplate: String
└─ actionType: ENUM (FAQ, ORDER_STATUS, HUMAN_HANDOFF)
```

会话上下文示例：
```json
{
  "sessionId": "user-001",
  "lastIntent": "faq_shipping",
  "slots": {
    "orderId": "A001"
  }
}
```

## 6. 接口定义

### POST `/api/dialogue/message`
- **请求体**
```json
{
  "sessionId": "user-001",
  "message": "想了解一下物流"
}
```
- **响应体**
```json
{
  "sessionId": "user-001",
  "reply": "我们已安排顺丰配送，1-3 天送达。",
  "intentId": "faq_shipping",
  "context": { ... }
}
```

## 7. 日志与监控
- 使用 `Slf4j` 输出结构化日志，包含 sessionId、intentId、耗时等。
- 暴露 `/actuator/health` 供存活检查。

## 8. 部署建议
- Demo 可使用 `mvn spring-boot:run` 启动。
- 生产可打包成 Docker 镜像，运行在 K8s / 云容器平台。

