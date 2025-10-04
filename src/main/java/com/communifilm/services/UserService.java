package com.communifilm.services;

import com.communifilm.models.User;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseToken;
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

    // Auth-based user creation
    public void saveUserFromAuth(FirebaseToken decodedToken) throws ExecutionException, InterruptedException {
        Map<String, Object> data = new HashMap<>();
        data.put("id", decodedToken.getUid());
        data.put("email", decodedToken.getEmail());
        data.put("displayName", decodedToken.getClaims().get("name"));
        data.put("profilePictureUrl", decodedToken.getClaims().get("picture"));
        data.put("createdAt", FieldValue.serverTimestamp());
        data.put("updatedAt", FieldValue.serverTimestamp());

        firestore.collection("users").document(decodedToken.getUid()).set(data).get();
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

