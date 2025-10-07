package com.communifilm.controllers;

import com.communifilm.dtos.LoginResponseDto;
import com.communifilm.dtos.UpdateUserDto;
import com.communifilm.models.User;
import com.communifilm.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.communifilm.services.GoogleAuthService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import java.util.ArrayList;
import java.util.List;
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
     * @return LoginResponseDto
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestHeader("Authorization") String authorizationHeader) throws Exception {
        String idToken = authorizationHeader.substring(7);
        GoogleIdToken.Payload payload = googleAuthService.verifyToken(idToken);

        boolean isNewUser = userService.processUserLogin(payload);
        User user = userService.getUser(payload.getSubject());

        return ResponseEntity.ok(new LoginResponseDto(isNewUser, user));
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers(@RequestParam List<String> uids) {
        try {
            List<User> users = new ArrayList<>();
            for (String uid : uids) {
                User user = userService.getUser(uid);
                if (user != null) {
                    users.add(user);
                }
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
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

    @PutMapping("/{uid}")
    public ResponseEntity<Void> updateUser(@PathVariable String uid, @RequestBody UpdateUserDto userDto) throws ExecutionException, InterruptedException {
        userService.updateUser(uid, userDto);
        return ResponseEntity.ok().build();
    }
}

