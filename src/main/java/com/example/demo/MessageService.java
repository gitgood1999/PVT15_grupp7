package com.example.demo;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public MessageRepository getMessageRepository() {
        return messageRepository;
    }

    public List<Message> getMessagesByChatId(long chatId){
        return messageRepository.findByChatId(chatId);
    }
}
