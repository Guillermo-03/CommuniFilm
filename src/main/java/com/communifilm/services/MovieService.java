package com.communifilm.services;

import com.communifilm.dtos.MovieDto;
import com.communifilm.dtos.TmdbResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final RestTemplate restTemplate; // Inject RestTemplate

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.base.url}")
    private String tmdbBaseUrl;

    public List<MovieDto> getTrendy() {
        // Build the URL for the TMDB API
        String url = UriComponentsBuilder.fromHttpUrl(tmdbBaseUrl + "/trending/movie/week")
                .queryParam("api_key", apiKey)
                .toUriString();

        // Make a synchronous GET request
        TmdbResponse response = restTemplate.getForObject(url, TmdbResponse.class);

        // Process the response
        if (response == null || response.getResults() == null) {
            return Collections.emptyList();
        }

        return response.getResults().stream()
                .map(r -> new MovieDto(r.getTitle(), r.getOverview()))
                .collect(Collectors.toList());
    }
}