package com.example.dialoguedemo.gateway.api;

import com.example.dialoguedemo.dm.DialogueManager;
import com.example.dialoguedemo.model.DialogueRequest;
import com.example.dialoguedemo.model.DialogueResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dialogue")
public class DialogueController {

    private final DialogueManager dialogueManager;

    public DialogueController(DialogueManager dialogueManager) {
        this.dialogueManager = dialogueManager;
    }

    @PostMapping("/message")
    public ResponseEntity<DialogueResponse> sendMessage(@Valid @RequestBody DialogueRequest request) {
        return ResponseEntity.ok(dialogueManager.handleMessage(request));
    }
}
