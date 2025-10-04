package com.communifilm.services;

import com.communifilm.dtos.MovieDto;
import com.communifilm.dtos.TmdbResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final WebClient tmdbWebClient;

    @Value("${tmdb.api.key}")
    private String apiKey;

    public Mono<List<MovieDto>> getTrendy() {
        return tmdbWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/trending/movie/week")
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

