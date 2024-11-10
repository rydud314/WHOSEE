package com.example.seesaw.model;

import java.util.List;

public class ChatGPTResponse {
    private List<Choice> choices;

    public List<Choice> getChoices() {
        return choices;
    }

    public static class Choice {
        private ChatMsg message;

        public ChatMsg getMessage() {
            return message;
        }
    }
}