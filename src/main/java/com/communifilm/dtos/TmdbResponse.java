package com.communifilm.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbResponse {
    private List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        private String title;    // For movies
        private String name;     // For TV shows
        private String overview;
        @JsonProperty("poster_path")
        private String posterPath;
        private long id;


        public String getTitle() {
            return title != null ? title : name; // fallback for TV shows
        }

        public String getOverview() {
            return overview;
        }

        public String getPosterPath(){
            return posterPath;
        }

        public long getId(){ return id;}

        public void setTitle(String title) {
            this.title = title;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setOverview(String overview) {
            this.overview = overview;
        }

        public void setPosterPath(String posterPath){
            this.posterPath = posterPath;
        }

        public void setId(long id){
            this.id = id;
        }
    }
}

