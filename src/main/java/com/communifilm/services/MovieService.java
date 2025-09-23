package com.communifilm.services;

import com.communifilm.dtos.MovieDto;
import com.communifilm.dtos.TmdbResponse;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class MovieService {

    @Value("${tmdb.api.key}")
    private String apiKey;

    private final WebClient webClient;

    public MovieService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.themoviedb.org/3").build();
    }

    public Mono<List<MovieDto>> getTrendy() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/trending/all/week")
                        .queryParam("api_key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(TmdbResponse.class)
                .map(response -> {
                    if (response == null || response.getResults() == null) {
                        return List.of();
                    }
                    return response.getResults().stream()
                            .map(r -> new MovieDto(r.getTitle(), r.getOverview()))
                            .toList();
                });
    }
}

