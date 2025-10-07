package com.communifilm.dtos;


public class MovieDto {
    private String title;
    private String overview;
    private String posterURL;
    private long id;

    public MovieDto() {}

    public MovieDto(String title, String overview, String posterURL, long id) {
        this.title = title;
        this.overview = overview;
        this.posterURL = posterURL;
        this.id = id;
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

    public long getId(){ return id ;}
}

