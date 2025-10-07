package com.communifilm.controllers;

import com.communifilm.dtos.CreateReviewDto;
import com.communifilm.models.MovieReview;
import com.communifilm.services.MovieReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/reviews")
public class MovieReviewController {

    private final MovieReviewService movieReviewService;

    public MovieReviewController(MovieReviewService movieReviewService) {
        this.movieReviewService = movieReviewService;
    }

    @PostMapping
    public MovieReview createReview(@RequestBody CreateReviewDto reviewDto) throws ExecutionException, InterruptedException {
        return movieReviewService.createReview(reviewDto);
    }

    @GetMapping("/movie/{movieId}")
    public List<MovieReview> getReviewsForMovie(@PathVariable Long movieId) throws ExecutionException, InterruptedException {
        return movieReviewService.getReviewsForMovie(movieId);
    }

}