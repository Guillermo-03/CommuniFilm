package com.communifilm.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

        public String getTitle() {
            return title != null ? title : name; // fallback for TV shows
        }

        public String getOverview() {
            return overview;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setOverview(String overview) {
            this.overview = overview;
        }
    }
}

