package com.communifilm.controllers;

import com.communifilm.models.User;
import com.communifilm.services.FirebaseService;
import com.communifilm.services.UserService;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/users")
public class UserController {

    private final FirebaseService firebaseService;
    private final UserService userService;

    public UserController(FirebaseService firebaseService, UserService userService) {
        this.firebaseService = firebaseService;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public String signup(@RequestHeader("Authorization") String idToken) throws Exception {
        if (idToken.startsWith("Bearer ")) {
            idToken = idToken.substring(7);
        }
        FirebaseToken decodedToken = firebaseService.verifyIdToken(idToken);
        userService.saveUserFromAuth(decodedToken);
        return "User signed up successfully: " + decodedToken.getUid();
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody User user) throws ExecutionException, InterruptedException {
        userService.createUser(user);
        return ResponseEntity.ok("User created");
    }

    @GetMapping("/{uid}")
    public ResponseEntity<User> getUser(@PathVariable String uid) {
        try {
            User user = userService.getUser(uid);
            if (user == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}

