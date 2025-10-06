package com.communifilm.services;

import com.communifilm.models.User;
import com.google.cloud.firestore.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.cloud.firestore.FieldValue;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    private final Firestore firestore;

    public UserService(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Handles user login. If it's the user's first time, a new record
     * is created in the database. Otherwise, no database write is performed.
     *
     * @param payload The payload from the verified Google ID token.
     */
    public void processUserLogin(GoogleIdToken.Payload payload) throws ExecutionException, InterruptedException {
        String userId = payload.getSubject();
        DocumentReference userRef = firestore.collection("users").document(userId);
        DocumentSnapshot snapshot = userRef.get().get();

        // Only create the user document if it does not already exist
        if (!snapshot.exists()) {
            Map<String, Object> newUser = new HashMap<>();
            newUser.put("uid", userId);
            newUser.put("email", payload.getEmail());
            newUser.put("displayName", payload.get("name"));
            newUser.put("profilePictureUrl", payload.get("picture"));
            newUser.put("createdAt", FieldValue.serverTimestamp()); // Set the creation timestamp
            newUser.put("updatedAt", FieldValue.serverTimestamp());

            // Create the new user in Firestore
            userRef.set(newUser).get();
        }
        // If the user already exists, no write to DB is required. Authentication is handled by the security filter
    }

    // Generic CRUD
    public String createUser(User user) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("users").document();
        Map<String,Object> data = new HashMap<>();
        data.put("displayName", user.getDisplayName());
        data.put("email", user.getEmail());
        data.put("profilePictureUrl", user.getProfilePictureUrl());
        data.put("createdAt", FieldValue.serverTimestamp());
        data.put("updatedAt", FieldValue.serverTimestamp());

        docRef.set(data).get();
        return docRef.getId();
    }

    public User getUser(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = firestore.collection("users").document(id).get().get();
        return snapshot.exists() ? snapshot.toObject(User.class) : null;
    }

    public void updateUser(User user) throws ExecutionException, InterruptedException {
        if (user.getUid() == null) throw new IllegalArgumentException("User ID cannot be null");
        Map<String,Object> data = new HashMap<>();
        data.put("displayName", user.getDisplayName());
        data.put("email", user.getEmail());
        data.put("profilePictureUrl", user.getProfilePictureUrl());
        data.put("updatedAt", FieldValue.serverTimestamp());

        firestore.collection("users").document(user.getUid()).update(data).get();
    }

    public void deleteUser(String id) throws ExecutionException, InterruptedException {
        firestore.collection("users").document(id).delete().get();
    }
}

