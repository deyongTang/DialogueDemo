# DialogueDemo

本项目提供一个基于 Java + Spring Boot 的企业对话系统 Demo，用于学习对话业务流程与系统架构。项目包含：

- `docs/Design.md`：设计文档，含架构图、业务流程、模块说明。
- `dialogue-demo` 应用：提供 `/api/dialogue/message` 接口，接收用户消息并返回意图匹配结果。

## 快速开始

```bash
mvn spring-boot:run
```

启动后可使用 `curl` 体验：

```bash
curl -X POST http://localhost:8080/api/dialogue/message \
  -H 'Content-Type: application/json' \
  -d '{"sessionId":"demo-user","message":"想了解物流"}'
```

返回示例：
```json
{
  "sessionId": "demo-user",
  "reply": "我们的订单由顺丰配送，通常 1-3 天送达。",
  "intentId": "faq_shipping",
  "context": {
    "lastUpdated": "2024-04-06T10:00:00Z"
  }
}
```

## 测试

```bash
mvn test
```

## 目录结构

```
├── docs/Design.md         # 设计文档
├── pom.xml                # Maven 配置
├── src/main               # Spring Boot 代码
├── src/test               # 单元测试
```
