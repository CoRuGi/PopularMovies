package com.cr.androidnanodegree.popularmovies;

/**
 * The request to start the task
 */
public class FetchMovieTaskRequest {

    String apiKey;
    String sortBy;
    int page;

    public FetchMovieTaskRequest(String apiKey) {
        setApiKey(apiKey);
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

}
