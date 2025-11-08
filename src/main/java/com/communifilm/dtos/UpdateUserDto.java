package com.communifilm.dtos;

import java.util.List;

public class UpdateUserDto {
    private String displayName;
    private String bio;
    private List<TopMovieInputDto> topMovies;

    // Getters and Setters
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<TopMovieInputDto> getTopMovies() {
        return topMovies;
    }

    public void setTopMovies(List<TopMovieInputDto> topMovies) {
        this.topMovies = topMovies;
    }
}