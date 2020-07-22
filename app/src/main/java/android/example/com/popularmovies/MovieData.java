package android.example.com.popularmovies;

import android.net.Uri;

import androidx.annotation.NonNull;

public class MovieData {
    //title, release date, movie poster, vote average, and plot synopsis.

    private String title;
    private String release_date;
    private String poster_path;
    //TODO: Determine whether to parse and store as a float or string.
    private String vote_average;
    private String overview;
    private Uri posterUri;

    public MovieData(String title, String release_date, String poster_path, String vote_average, String overview) {
        this.title = title;
        this.release_date = release_date;
        this.poster_path = poster_path;
        this.vote_average = vote_average;
        this.overview = overview;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Uri getPosterUri() {
        return posterUri;
    }

    public void setPosterUri(Uri posterUri) {
        this.posterUri = posterUri;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("Title: %s Overview: %s Poster Uri: %s Release Date: %s Vote Avg: %s",
                title,
                overview,
                posterUri.toString(),
                release_date,
                vote_average
                );
    }
}
