package com.communifilm.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteMovie {
    private int rank;           // 1, 2, or 3
    private long movieId;       // TMDB movie ID
    private String title;       // Cached from TMDB for display
    private String posterURL;   // Cached from TMDB for display
    private String releaseDate; // Cached from TMDB for display
    private double voteAverage; // Cached from TMDB for display
    private String overview;    // Cached from TMDB for display
}
