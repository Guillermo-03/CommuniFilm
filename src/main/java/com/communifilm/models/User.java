package com.communifilm.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String uid;
    private String email;
    private String displayName;
    private String bio;
    private String profilePictureUrl;
    private List<FavoriteMovie> topMovies;
    private Instant createdAt;
    private Instant updatedAt;
}
