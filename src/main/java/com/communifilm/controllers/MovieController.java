package com.communifilm.controllers;

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
    public List<MovieDto> getTrendy(@RequestParam(required = false, defaultValue = "false") boolean trending) {
        if (trending) {
            return movieService.getTrendy();
        }
        return Collections.emptyList();
    }
}