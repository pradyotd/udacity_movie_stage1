package android.example.com.popularmovies;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieWebService {

    @GET("/3/movie/popular")
    Call<MovieResult> getPopularMovies(@Query("api_key")String  apikey);

    @GET("/3/movie/top_rated")
    Call<MovieResult> getTopRatedMovies(@Query("api_key")String  apikey);

    @GET("/3/movie/{movie_id}/videos")
    Call<VideoResult> getTrailers(@Path("movie_id") int movieId, @Query("api_key") String  apikey);

    @GET("/3/movie/{movie_id}/reviews")
    Call<ReviewResult> getReviews(@Path("movie_id") int movieId, @Query("api_key") String  apikey);

}
