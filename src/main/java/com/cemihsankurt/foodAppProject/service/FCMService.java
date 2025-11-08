package com.cemihsankurt.foodAppProject.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FCMService {

    @Autowired
    private FirebaseMessaging firebaseMessaging; // Bu, FCMConfig sayesinde otomatik gelir

    /**
     * Belirli bir cihaz token'ına anlık bildirim gönderir.
     */
    public void sendPushNotification(String fcmToken, String title, String body) {
        if (fcmToken == null || fcmToken.isEmpty()) {
            System.err.println("FCM Token not found, can not send notification");
            return;
        }

        // Bildirimin başlığı ve içeriği
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        // Mesajı oluştur
        Message message = Message.builder()
                .setToken(fcmToken) // Hangi cihaza gideceği
                .setNotification(notification)
                // (İsteğe bağlı: 'data' alanı ile uygulamayı açınca
                //  doğrudan sipariş detayına yönlendirebilirsin)
                // .putData("orderId", "12345")
                .build();

        try {
            // Mesajı gönder
            firebaseMessaging.send(message);
            System.out.println("FCM bildirimi başarıyla gönderildi: " + fcmToken);
        } catch (Exception e) {
            System.err.println("FCM bildirimi gönderilirken hata: " + e.getMessage());
        }
    }
}
