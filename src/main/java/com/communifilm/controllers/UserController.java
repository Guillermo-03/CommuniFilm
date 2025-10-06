package com.communifilm.controllers;

import com.communifilm.models.User;
import com.communifilm.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.communifilm.services.GoogleAuthService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/users")
public class UserController {

    private final GoogleAuthService googleAuthService;
    private final UserService userService;

    public UserController(GoogleAuthService googleAuthService, UserService userService) {
        this.googleAuthService = googleAuthService;
        this.userService = userService;
    }

    /**
     * Endpoint for users to sign in. It verifies the Google token and
     * creates a user record on the first login.
     *
     * @param authorizationHeader The "Authorization: Bearer <ID_TOKEN>" header.
     * @return A success message with the user's ID.
     */
    @PostMapping("/login")
    public String login(@RequestHeader("Authorization") String authorizationHeader) throws Exception {
        String idToken = authorizationHeader.substring(7);
        GoogleIdToken.Payload payload = googleAuthService.verifyToken(idToken);

        // This will create the user only if they don't exist
        userService.processUserLogin(payload);

        return "User authenticated successfully: " + payload.getSubject();
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

