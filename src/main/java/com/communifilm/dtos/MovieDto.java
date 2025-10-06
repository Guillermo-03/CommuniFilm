package com.communifilm.dtos;


public class MovieDto {
    private String title;
    private String overview;
    private String posterURL;

    public MovieDto() {}

    public MovieDto(String title, String overview, String posterURL) {
        this.title = title;
        this.overview = overview;
        this.posterURL = posterURL;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterURL() {
        return posterURL;
    }
}

