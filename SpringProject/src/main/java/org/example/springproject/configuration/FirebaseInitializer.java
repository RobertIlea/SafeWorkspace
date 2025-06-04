/**
 * FirebaseInitializer.java
 * This class initializes Firebase with the service account credentials and provides a Firestore client bean.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * FirebaseInitializer class is responsible for initializing Firebase with the service account credentials.
 * It is marked as a Spring configuration class with the @Configuration annotation so that the Spring container can recognize it.
 */
@Configuration
public class FirebaseInitializer {

    /**
     * The URL of the Firebase Realtime Database.
     * This is used to connect to the Firebase database.
     * @Value annotation is used to inject the value from application properties.
     */
    @Value("${firebase.database.url}")
    private String dbUrl;

    /**
     * Creates a Firestore client bean.
     * This method initializes Firebase with the service account credentials and returns a Firestore client.
     * It reads the service account key from the environment variable "ROOM_MONITORING_SYSTEM".
     * @return Firestore client
     * @throws IOException if there is an error reading the service account key
     */
    @Bean
    public Firestore getDb() throws IOException {
        // Load the service account key from the classpath
        String serviceAccountKey = System.getenv("ROOM_MONITORING_SYSTEM");
        FileInputStream serviceAccount = new FileInputStream(serviceAccountKey);

        // Initialize Firebase with the service account credentials
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(dbUrl)
                .build();

        // Initialize FirebaseApp only if it's not initialized yet
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        // Return Firestore client
        return FirestoreClient.getFirestore();
    }
}
