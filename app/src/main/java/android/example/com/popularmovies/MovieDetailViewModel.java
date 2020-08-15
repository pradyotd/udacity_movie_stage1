package android.example.com.popularmovies;

import android.app.Application;
import android.util.Log;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;


public class MovieDetailViewModel extends AndroidViewModel {
    private final static String TAG = MovieDetailViewModel.class.getName();
    private MovieDataRepository movieDataRepository;
    private AppDatabase mDb;
    private static final  String seperator = "\n";

    public MovieDetailViewModel(Application application) {
        super(application);
        Log.i(TAG, "Constructing Detail ViewModel");
        mDb = AppDatabase.getInstance(application);

        movieDataRepository = MovieDataRepository.getInstance(mDb, application.getString(R.string.MOVIEDB_API_KEY));
    }

    public LiveData<MovieData> getMovieData(int movieId){
        return movieDataRepository.fetchMovieData(movieId);
    }

    public LiveData<List<MovieDetails>> getMovieIsFavorite(int movieId) {
        return mDb.movieDao().getMovie(movieId);
    }

    public LiveData<String> getReviews(int movieId){
        Log.i(TAG, "getReviews:" + movieId);
        LiveData<List<Review>> reviews = movieDataRepository.fetchReviews(movieId);
        return Transformations.map(reviews, allReviews -> {
            StringBuilder builder = new StringBuilder();
            allReviews.forEach(review -> {
                builder.append("Review By: " + review.getAuthor());
                builder.append(seperator);
                builder.append(review.getContent());
                builder.append(seperator);
                builder.append(seperator);
            });
            return builder.toString();
        });
    }

    public LiveData<List<Video>> getTrailers(int movieId){
        return movieDataRepository.fetchTrailers(movieId);
    }

    public void applyFavorite(int movieId){
        movieDataRepository.createFavorite(movieId);
    }

    public void clearFavorite(int movieId){
        movieDataRepository.deleteFavorite(movieId);
    }


}
