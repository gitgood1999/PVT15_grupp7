package com.example.demo;

import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final FCMService fcmService;

    public MessageController(MessageService messageService,
                             ChatRepository chatRepository,
                             UserRepository userRepository,
                             SimpMessagingTemplate messagingTemplate,
                             FCMService fcmService) {
        this.messageService = messageService;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.fcmService = fcmService;
    }

    @GetMapping("/chat/{chatId}")
    public List<MessageDTO> getMessagesByChat(@PathVariable Long chatId) {
        List<Message> all = messageService.getMessagesByChatId(chatId);
        return all.stream()
                .map(msg -> new MessageDTO(
                        msg.getChat().getId(),
                        msg.getSender().getId(),
                        msg.getContent(),
                        msg.getTimestamp()
                ))
                .toList();
    }



    @PostMapping
    public ResponseEntity<Message> createMessage(@RequestBody MessageDTO dto) throws FirebaseMessagingException {
        Optional<Chat> chatOpt = chatRepository.findById(dto.getChatId());
        Optional<User> senderOpt = userRepository.findById(dto.getSenderId());
        if (chatOpt.isEmpty() || senderOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Chat chat = chatOpt.get();
        User sender = senderOpt.get();

        // Bygg och spara meddelandet i databasen
        Message msg = new Message();
        msg.setChat(chat);
        msg.setSender(sender);
        msg.setContent(dto.getContent());
        msg.setTimestamp(LocalDateTime.now());
        Message saved = messageService.getMessageRepository().save(msg);

        // 1) WebSocket: skicka till alla på /topic/chat/{id}
        ChatMessage wsMsg = new ChatMessage();
        wsMsg.setChatId(chat.getId());
        wsMsg.setSenderId(sender.getId());
        wsMsg.setFrom(sender.getName());
        wsMsg.setContent(saved.getContent());
        messagingTemplate.convertAndSend("/topic/chat/" + chat.getId(), wsMsg);

        // 2) Push-notis via FCM
        // Hämta match-objektet
        UserMatch match = chat.getMatch();
        User partner;
        // Avgör vem som är partnern
        if (match.getUser1().getId()==(sender.getId())) {
            partner = match.getUser2();
        } else {
            partner = match.getUser1();
        }
        // Läs partnerns sparade token
        String deviceToken = partner.getFcmToken();
        if (deviceToken != null && !deviceToken.isEmpty()) {
            fcmService.sendMessage(
                    deviceToken,
                    "Nytt meddelande från " + sender.getName(),
                    saved.getContent()
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @MessageMapping("/echo")
    @SendTo("/topic/echo")
    public String echo(String msg) {
        return msg;
    }

}