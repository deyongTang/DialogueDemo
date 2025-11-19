package com.example.dialoguedemo.controller;

import com.example.dialoguedemo.model.DialogueRequest;
import com.example.dialoguedemo.model.DialogueResponse;
import com.example.dialoguedemo.service.DialogueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dialogue")
public class DialogueController {

    private final DialogueService dialogueService;

    public DialogueController(DialogueService dialogueService) {
        this.dialogueService = dialogueService;
    }

    @PostMapping("/message")
    public ResponseEntity<DialogueResponse> sendMessage(@Valid @RequestBody DialogueRequest request) {
        return ResponseEntity.ok(dialogueService.handleMessage(request));
    }
}
