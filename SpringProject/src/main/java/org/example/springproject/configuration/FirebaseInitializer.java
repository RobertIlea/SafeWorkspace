package org.example.springproject.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseInitializer {

    @Bean
    public Firestore getDb() throws IOException {
        // Load the service account key from the classpath
        String serviceAccountKey = System.getenv("SPACE_MONITORING_SYSTEM");
        FileInputStream serviceAccount =
                new FileInputStream(serviceAccountKey);

        // Initialize Firebase with the service account credentials
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://spacemonitoringsystem-default-rtdb.europe-west1.firebasedatabase.app")
                .build();

        // Initialize FirebaseApp only if it's not initialized yet
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        // Return Firestore client
        return FirestoreClient.getFirestore();
    }
}
