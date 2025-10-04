package com.communifilm.configs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirestoreConfig {

    @Value("${firebase.credentials.path}")
    private String firebaseCredentialsPath;

    @Value("${firebase.project-id}")
    private String firebaseProjectId;

    @Bean
    public Firestore firestore() throws IOException {
        // Load service account key
        FileInputStream serviceAccount = new FileInputStream(firebaseCredentialsPath);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setProjectId(firebaseProjectId)
                .build();

        // Initialize Firebase app only once
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        System.out.println("☁️ Connected to Firebase CLOUD Firestore project: " + firebaseProjectId);

        return FirestoreClient.getFirestore();
    }
}



