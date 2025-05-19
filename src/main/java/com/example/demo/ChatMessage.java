package com.example.demo;

public class ChatMessage {
    private Long chatId;
    private Long senderId;
    private String from;
    private String content;

    public ChatMessage() {}

    // getters + setters
    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
