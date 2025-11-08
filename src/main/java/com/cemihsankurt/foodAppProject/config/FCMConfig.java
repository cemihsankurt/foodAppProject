package com.cemihsankurt.foodAppProject.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FCMConfig {

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {

        String serviceAccountPath =  "foodappproject-186ac-firebase-adminsdk-fbsvc-e8dc6470dd.json";

        ClassPathResource resource = new ClassPathResource(serviceAccountPath);
        InputStream serviceAccount = resource.getInputStream();

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();


        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        } else {
            return FirebaseApp.getInstance();
        }

    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        // Fabrikayı (firebaseApp) kullanarak ürünü (Messaging) al ve bean olarak döndür.
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
