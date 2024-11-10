package com.example.seesaw.model;

public class ChatMsg {
    // 역할을 나타내는 상수
    public static final String ROLE_USER = "user"; // 내 메시지
    public static final String ROLE_ASSISTANT = "assistant"; // 챗봇 메시지

    // 메시지 유형을 나타내는 상수
    public static final int TYPE_MY_CHAT = 0;
    public static final int TYPE_BOT_CHAT = 1;

    public String role; // 누가 보낸 메시지인지 확인
    public String content; // 메시지 내용

    // 생성자
    public ChatMsg(String role, String content) {
        this.role = role;
        this.content = content;
    }

    // 타입에 해당하는 역할을 반환하는 메서드
    public int getMessageType() {
        if (ROLE_USER.equals(role)) {
            return TYPE_MY_CHAT; // 내 메시지
        } else if (ROLE_ASSISTANT.equals(role)) {
            return TYPE_BOT_CHAT; // 챗봇 메시지
        }
        return -1; // 잘못된 타입일 경우
    }
}
