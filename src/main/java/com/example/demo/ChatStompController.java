package com.example.demo;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Controller
@MessageMapping("/chat")                 // prefix för inkommande /app/chat/…
public class ChatStompController {

    private final SimpMessagingTemplate tpl;

    public ChatStompController(SimpMessagingTemplate tpl) {
        this.tpl = tpl;
    }

    @MessageMapping("/{chatId}")            // lyssnar på SEND /app/chat/{chatId}
    public void onMessage(
            @DestinationVariable Long chatId,
            ChatMessage msg
    ) {
        // Skicka ut till alla som är SUBSCRIBEd på /topic/chat/{chatId}
        tpl.convertAndSend("/topic/chat/" + chatId, msg);
    }
}
