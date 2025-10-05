package com.communifilm.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String uid;
    private String email;
    private String displayName;
    private String profilePictureUrl;
    private Instant createdAt;        // optional for Java use
    private Instant updatedAt;        // optional for Java use
}
