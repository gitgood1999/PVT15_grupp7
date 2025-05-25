package com.example.demo;


import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FCMService {

    public String sendMessage(String token, String title, String body, Long chatId) throws FirebaseMessagingException {
        // 1) Bygg själva notisen
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        // 2) Android-specifika inställningar: hög prioritet, default-ljud
        AndroidConfig androidConfig = AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder()
                        .setSound("default")
                        .build()  // <- bara ljud, ingen kanal
                )
                .build();


        // 3) iOS (APNs): background-fetch och default-ljud
        ApnsConfig apnsConfig = ApnsConfig.builder()
                .setAps(Aps.builder()
                        .setContentAvailable(true)  // gör att iOS kan väcka appen
                        .setSound("default")
                        .build()
                )
                .build();

        // 4) Bygg hela meddelandet med data så klienten vet vilken chat det gäller
        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .setAndroidConfig(androidConfig)
                .setApnsConfig(apnsConfig)
                .putData("chatId", chatId.toString())
                .putData("screen", "chat")
                .build();

        // 5) Skicka
        return FirebaseMessaging.getInstance().send(message);
    }
}
