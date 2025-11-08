package com.communifilm.services;

import com.communifilm.dtos.MovieDetailDto;
import com.communifilm.dtos.TopMovieInputDto;
import com.communifilm.dtos.UpdateUserDto;
import com.communifilm.models.FavoriteMovie;
import com.communifilm.models.User;
import com.google.cloud.firestore.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.cloud.firestore.FieldValue;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final Firestore firestore;
    private final MovieService movieService;

    public UserService(Firestore firestore, MovieService movieService) {
        this.firestore = firestore;
        this.movieService = movieService;
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

        // Handle top movies update
        if (userDto.getTopMovies() != null) {
            List<FavoriteMovie> favoriteMovies = processTopMovies(userDto.getTopMovies());
            data.put("topMovies", favoriteMovies);
        }

        data.put("updatedAt", FieldValue.serverTimestamp());

        firestore.collection("users").document(uid).update(data).get();
    }

    /**
     * Validates and processes top movies input.
     * Fetches movie details from TMDB and creates FavoriteMovie objects.
     */
    private List<FavoriteMovie> processTopMovies(List<TopMovieInputDto> topMoviesInput) {
        // Validate list size (0-3 movies)
        if (topMoviesInput.size() > 3) {
            throw new IllegalArgumentException("Cannot have more than 3 top movies");
        }

        // Validate ranks are unique and in valid range
        Set<Integer> ranks = new HashSet<>();
        for (TopMovieInputDto input : topMoviesInput) {
            if (input.getRank() < 1 || input.getRank() > 3) {
                throw new IllegalArgumentException("Rank must be between 1 and 3");
            }
            if (!ranks.add(input.getRank())) {
                throw new IllegalArgumentException("Duplicate rank found: " + input.getRank());
            }
        }

        // Fetch movie details from TMDB and build FavoriteMovie objects
        return topMoviesInput.stream()
                .map(input -> {
                    try {
                        MovieDetailDto movieDetails = movieService.getMovieDetails((int) input.getMovieId());
                        if (movieDetails == null) {
                            throw new IllegalArgumentException("Movie not found with ID: " + input.getMovieId());
                        }
                        return FavoriteMovie.builder()
                                .rank(input.getRank())
                                .movieId(input.getMovieId())
                                .title(movieDetails.getTitle())
                                .posterURL(movieDetails.getPosterURL())
                                .releaseDate(movieDetails.getReleaseDate())
                                .voteAverage(movieDetails.getVoteAverage())
                                .overview(movieDetails.getOverview())
                                .build();
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to fetch movie details for ID: " + input.getMovieId(), e);
                    }
                })
                .collect(Collectors.toList());
    }
}

