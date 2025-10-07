package com.communifilm.services;

import com.communifilm.dtos.MovieDetailDto;
import com.communifilm.dtos.MovieDto;
import com.communifilm.dtos.TmdbResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {
    private static final String TMDB_IMG_BASE_URL =  "https://image.tmdb.org/t/p/w500";

    private final RestTemplate restTemplate;

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.base.url}")
    private String tmdbBaseUrl;

    public List<MovieDto> getTrendy() {
        String url = UriComponentsBuilder.fromUriString(tmdbBaseUrl + "/trending/movie/week")
                .queryParam("api_key", apiKey)
                .toUriString();
        return processTmdbResponse(url);
    }

    public List<MovieDto> searchMovies(String query) {
        String url = UriComponentsBuilder.fromUriString(tmdbBaseUrl + "/search/movie")
                .queryParam("api_key", apiKey)
                .queryParam("query", query)
                .toUriString();
        return processTmdbResponse(url);
    }

    public MovieDetailDto getMovieDetails(int movieId) {
        String url = UriComponentsBuilder.fromUriString(tmdbBaseUrl + "/movie/" + movieId)
                .queryParam("api_key", apiKey)
                .toUriString();

        MovieDetailDto movieDetails = restTemplate.getForObject(url, MovieDetailDto.class);
        if (movieDetails != null) {
            movieDetails.setPosterURL(toPosterURL(movieDetails.getPosterPath()));
        }

        return movieDetails;
    }

    private List<MovieDto> processTmdbResponse(String url) {
        TmdbResponse response = restTemplate.getForObject(url, TmdbResponse.class);

        if (response == null || response.getResults() == null) {
            return Collections.emptyList();
        }

        return response.getResults().stream()
                .map(r -> new MovieDto(
                        r.getTitle(),
                        r.getOverview(),
                        toPosterURL(r.getPosterPath()),
                        r.getId()
                ))
                .collect(Collectors.toList());
    }

    private static String toPosterURL(String path){
        if (path == null || path.isBlank()){
            return null;
        }

        return TMDB_IMG_BASE_URL + path;
    }
}