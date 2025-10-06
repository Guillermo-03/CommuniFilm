package com.communifilm.controllers;

import com.communifilm.dtos.MovieDetailDto;
import com.communifilm.dtos.MovieDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.communifilm.services.MovieService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<MovieDto> getMovies(@RequestParam(required = false) String trending, @RequestParam(required = false) String search) {
        if (Boolean.parseBoolean(trending)) {
            return movieService.getTrendy();
        }
        if (search != null && !search.isEmpty()) {
            return movieService.searchMovies(search);
        }
        return Collections.emptyList();
    }

    @GetMapping("/{movieId}")
    public MovieDetailDto getMovieById(@PathVariable int movieId) {
        return movieService.getMovieDetails(movieId);
    }
}