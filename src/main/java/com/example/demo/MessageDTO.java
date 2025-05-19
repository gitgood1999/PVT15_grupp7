package com.example.demo;

import java.time.LocalDateTime;

public class MessageDTO {
    private Long chatId;
    private Long senderId;
    private String content;
    private LocalDateTime timestamp;
    public MessageDTO() {}


    public MessageDTO(Long chatId, Long senderId, String content, LocalDateTime timestamp) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
    }


    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}