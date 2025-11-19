package com.example.dialoguedemo.service;

import com.example.dialoguedemo.model.DialogueRequest;
import com.example.dialoguedemo.model.DialogueResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DialogueServiceTest {

    @Autowired
    private DialogueService dialogueService;

    @Test
    void shouldMatchIntentWithKeyword() {
        DialogueRequest request = new DialogueRequest();
        request.setSessionId("test");
        request.setMessage("想了解订单进度");

        DialogueResponse response = dialogueService.handleMessage(request);

        assertThat(response.getIntentId()).isEqualTo("order_status");
        assertThat(response.getReply()).contains("订单");
    }

    @Test
    void shouldReturnFallbackWhenNoMatch() {
        DialogueRequest request = new DialogueRequest();
        request.setSessionId("test2");
        request.setMessage("随机输入");

        DialogueResponse response = dialogueService.handleMessage(request);

        assertThat(response.getIntentId()).isEqualTo("fallback");
        assertThat(response.getReply()).contains("抱歉");
    }
}
