package com.example.dialoguedemo.dm;

import com.example.dialoguedemo.model.DialogueRequest;
import com.example.dialoguedemo.model.DialogueResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DialogueManagerTest {

    @Autowired
    private DialogueManager dialogueManager;

    @Test
    void shouldCompleteOrderStatusAfterSlotFilling() {
        DialogueRequest first = new DialogueRequest();
        first.setSessionId("test-order");
        first.setMessage("想了解订单进度");

        DialogueResponse askForSlot = dialogueManager.handleMessage(first);

        assertThat(askForSlot.getIntentId()).isEqualTo("order_status");
        assertThat(askForSlot.getReply()).contains("订单号");

        DialogueRequest second = new DialogueRequest();
        second.setSessionId("test-order");
        second.setMessage("订单号 A001");

        DialogueResponse status = dialogueManager.handleMessage(second);

        assertThat(status.getIntentId()).isEqualTo("order_status");
        assertThat(status.getReply()).contains("订单");
    }

    @Test
    void shouldReturnFallbackWhenNoMatch() {
        DialogueRequest request = new DialogueRequest();
        request.setSessionId("test2");
        request.setMessage("随机输入");

        DialogueResponse response = dialogueManager.handleMessage(request);

        assertThat(response.getIntentId()).isEqualTo("fallback");
        assertThat(response.getReply()).contains("抱歉");
    }

    @Test
    void shouldInvokeTrainSkill() {
        DialogueRequest first = new DialogueRequest();
        first.setSessionId("travel");
        first.setMessage("我要去北京");

        DialogueResponse askOrigin = dialogueManager.handleMessage(first);
        assertThat(askOrigin.getReply()).contains("出发城市");

        DialogueRequest second = new DialogueRequest();
        second.setSessionId("travel");
        second.setMessage("武汉");

        DialogueResponse askDate = dialogueManager.handleMessage(second);
        assertThat(askDate.getReply()).contains("出发日期");

        DialogueRequest third = new DialogueRequest();
        third.setSessionId("travel");
        third.setMessage("明天走");

        DialogueResponse ticketList = dialogueManager.handleMessage(third);

        assertThat(ticketList.getIntentId()).isEqualTo("book_train");
        assertThat(ticketList.getDomain()).isEqualTo("travel");
        assertThat(ticketList.getReply()).contains("12306").contains("北京").contains("武汉").contains("明天");
    }
}
