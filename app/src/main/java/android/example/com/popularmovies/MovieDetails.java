package android.example.com.popularmovies;


import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

//NOTE: we are storing movie details for favorites.
//This will allow us to look up favorites in the main menu.
//As well as display details. when

@Entity(tableName = "movie_details_table", primaryKeys = {"movie_id"})
public class MovieDetails {
    @ColumnInfo(name = "movie_id")
    private Integer movieId;
    private String title;
    @ColumnInfo(name = "release_date")
    private String releaseDate;
    @ColumnInfo(name = "vote_average")
    private String voteAverage;
    private String overview;
    @ColumnInfo(name = "poster_uri")
    private String posterUri;

    public MovieDetails(Integer movieId, String title, String releaseDate, String voteAverage, String overview, String posterUri) {
        this.movieId = movieId;
        this.title = title;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.posterUri = posterUri;
    }

    @Ignore
    public MovieDetails(MovieData movieData) {
        this.movieId = movieData.getId();
        this.title = movieData.getTitle();
        this.releaseDate = movieData.getRelease_date();
        this.voteAverage = movieData.getVote_average();
        this.overview = movieData.getOverview();
        this.posterUri = movieData.getPosterUri().toString();
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterUri() {
        return posterUri;
    }

    public void setPosterUri(String posterUri) {
        this.posterUri = posterUri;
    }
}
