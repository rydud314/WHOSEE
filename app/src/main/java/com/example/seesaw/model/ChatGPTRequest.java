package com.example.seesaw.model;

import java.util.List;

public class ChatGPTRequest {
    private String model;
    private List<ChatMsg> messages;

    public ChatGPTRequest(String model, List<ChatMsg> messages) {
        this.model = model;
        this.messages = messages;
    }
}