package com.communifilm.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopMovieInputDto {
    private int rank;       // 1, 2, or 3
    private long movieId;   // TMDB movie ID
}
