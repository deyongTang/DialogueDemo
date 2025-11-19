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
  "domain": "faq",
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

## 模块划分

- **渠道网关层**：`gateway.api.DialogueController` 提供 REST 接口，代表渠道网关/客服入口。
- **自然语言理解（NLU）**：`nlu.config.IntentConfigLoader` + `nlu.IntentMatcher` 载入 Rasa 风格配置，完成领域 Domain、意图 Intent、槽位 Slot 的解析。
- **对话管理（DM）**：`dm.DialogueManager` 作为管道核心，内部包含状态跟踪 DST（`dm.state.DialogueStateTracker`）和策略决策 DPL（`dm.policy.DialoguePolicy`）。
- **自然语言生成（NLG）**：`nlg.TemplateNlgService` 将策略输出渲染为最终回复，可替换为更智能的 NLG 模块。
- **上下文存储**：`context.ConversationContextStore` 管理会话槽位和多轮状态，目前为内存 Map，可替换 Redis。
- **技能适配层**：`skill.*` 以适配器方式封装业务“技能”，Demo 中提供 12306 订票示例。
- **数据协议**：`model.*` 中定义 `DialogueRequest/Response`、`IntentDefinition`、`ActionType` 等共享模型。

整个 Pipeline 为「NLU（Domain/Intent/Slot）→ DM（DST + DPL）→ NLG」，可支撑多轮补槽：当必填槽位缺失时，策略层会下发提示，用户补齐信息后由状态跟踪模块写入上下文，再进入下一步策略执行。

### 出行订票示例
1. 用户输入“我要去北京”。NLU 命中意图 `book_train`（领域 `travel`），提取 `destination=北京`。  
2. 策略层发现 `departureCity`、`departureDate` 尚未填写，依次返回提示：“请告诉我出发城市…”，“请告知出发日期…”。  
3. 用户可以直接回复城市名（如“武汉”）和日期（“明天走”）来填槽；NLG 在 `actionType=SKILL` 时委托 `TrainTicketSkillAdapter` 模拟拉起 12306，生成“正在为您打开 12306，查询 明天 从 武汉 前往 北京 …”的车次列表。

## 配置意图

- `src/main/resources/intents.yml` 采用 Rasa `nlu.yml` 风格维护训练语句，示例：

```yaml
version: "3.1"
nlu:
  - intent: faq_shipping
    metadata:
      actionType: FAQ
      replyTemplate: "我们的订单由顺丰配送，通常 1-3 天送达。"
    examples: |
      - 想了解物流
      - 快递什么时候到
```

- `metadata` 段中填写领域 (`domain`)、业务动作 (`actionType`) 及回复模板 (`replyTemplate`)，当 `actionType=SKILL` 时可通过 `skillName` 定位适配器；`slots` 块用于描述必填槽位（含 `keywords`/`pattern` 及 `prompt`），方便在 NLU 阶段提取以及 DM 多轮补槽。
