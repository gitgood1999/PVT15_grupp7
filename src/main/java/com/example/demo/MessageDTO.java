package com.example.demo;

public class MessageDTO {
    private Long chatId;
    private Long senderId;
    private String content;

    public MessageDTO(Long chatId, Long senderId, String content) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.content = content;
    }

    public Long getChatId() {
        return chatId;
    }
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
    public Long getSenderId() {
        return senderId;
    }
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
