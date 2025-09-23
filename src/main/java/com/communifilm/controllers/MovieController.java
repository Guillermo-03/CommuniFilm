package com.communifilm.controllers;

import com.communifilm.dtos.MovieDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import com.communifilm.services.MovieService;

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
    public Mono<List<MovieDto>> getTrendy(@RequestParam(required = false, defaultValue = "false") boolean trending) {
        if (trending) {
            return movieService.getTrendy();
        }
        return Mono.just(List.of()); // return empty list wrapped in a Mono
    }
}

