package com.communifilm.services;

import com.communifilm.dtos.UpdateUserDto;
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
     * Handles user login. Returns true if a new user was created.
     */
    public boolean processUserLogin(GoogleIdToken.Payload payload) throws ExecutionException, InterruptedException {
        String userId = payload.getSubject();
        DocumentReference userRef = firestore.collection("users").document(userId);
        DocumentSnapshot snapshot = userRef.get().get();

        if (!snapshot.exists()) {
            Map<String, Object> newUser = new HashMap<>();
            newUser.put("uid", userId);
            newUser.put("email", payload.getEmail());
            newUser.put("displayName", payload.get("name"));
            newUser.put("profilePictureUrl", payload.get("picture"));
            newUser.put("bio", null); // Explicitly set bio to null for new users
            newUser.put("createdAt", FieldValue.serverTimestamp());
            newUser.put("updatedAt", FieldValue.serverTimestamp());

            userRef.set(newUser).get();
            return true; // A new user was created
        }
        return false; // The user already existed
    }

    public User getUser(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = firestore.collection("users").document(id).get().get();
        return snapshot.exists() ? snapshot.toObject(User.class) : null;
    }

    /**
     * Updates user information from a DTO.
     */
    public void updateUser(String uid, UpdateUserDto userDto) throws ExecutionException, InterruptedException {
        if (uid == null) throw new IllegalArgumentException("User ID cannot be null");

        Map<String, Object> data = new HashMap<>();
        if (userDto.getDisplayName() != null) {
            data.put("displayName", userDto.getDisplayName());
        }
        if (userDto.getBio() != null) {
            data.put("bio", userDto.getBio());
        }
        data.put("updatedAt", FieldValue.serverTimestamp());

        firestore.collection("users").document(uid).update(data).get();
    }
}

