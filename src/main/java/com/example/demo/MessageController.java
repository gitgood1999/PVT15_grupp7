package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public MessageController(MessageService messageService, ChatRepository chatRepository, UserRepository userRepository) {
        this.messageService = messageService;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/chat/{chatId}")
    public List<Message> getMessagesByChat(@PathVariable Long chatId) {
        return messageService.getMessagesByChatId(chatId);
    }

    @PostMapping
    public ResponseEntity<Message> createMessage(@RequestBody MessageDTO dto) {
        Optional<Chat> chatOpt = chatRepository.findById(dto.getChatId());
        Optional<User> senderOpt = userRepository.findById(dto.getSenderId());

        if (chatOpt.isEmpty() || senderOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Chat chat = chatOpt.get();
        User sender = senderOpt.get();
        UserMatch match = chat.getMatch(); // Assuming Chat has @OneToOne UserMatch match

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(dto.getContent());
        message.setTimestamp(LocalDateTime.now());

        Message saved = messageService.getMessageRepository().save(message);
        return ResponseEntity.ok(saved);
    }


}

