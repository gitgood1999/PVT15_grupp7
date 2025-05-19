package com.example.demo;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /** Hämtar hela tråden i rätt ordning */
    public List<Message> getMessagesByChatId(Long chatId) {
        return messageRepository.findByChat_IdOrderByTimestampAsc(chatId);
    }

    public MessageRepository getMessageRepository() {
        return messageRepository;
    }
}
