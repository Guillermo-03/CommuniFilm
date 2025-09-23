package com.communifilm.dtos;


public class MovieDto {
    private String title;
    private String overview;

    public MovieDto() {}

    public MovieDto(String title, String overview) {
        this.title = title;
        this.overview = overview;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }
}

