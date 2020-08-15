package android.example.com.popularmovies;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface MovieDao {
    @Query("select * from movie_details_table")
    List<MovieDetails> getFavorites();

    @Query("select * from movie_details_table where movie_id = :movieId")
    LiveData<List<MovieDetails>> getMovie(int movieId);


    @Insert
    void insert(MovieDetails movieDetail);

    //Use this to un-favorite.
    @Query("delete from movie_details_table where movie_id = :movieId")
    void deleteMovie(int movieId);

}
