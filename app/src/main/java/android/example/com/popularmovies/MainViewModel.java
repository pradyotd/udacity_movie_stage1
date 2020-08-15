package android.example.com.popularmovies;

import android.app.Application;
import android.util.Log;

import java.util.List;


import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;



public class MainViewModel extends AndroidViewModel {
    private final static String TAG = MainViewModel.class.getName();

    private MovieDataRepository movieDataRepository;
    private AppDatabase mDb;


    public MainViewModel(Application application) {
        super(application);
        Log.i(TAG, "Constructing ViewModel");
        mDb = AppDatabase.getInstance(application);
        movieDataRepository = MovieDataRepository.getInstance(mDb, application.getString(R.string.MOVIEDB_API_KEY));
    }

    public LiveData<List<MovieData>> getMovies() {
        Log.i(TAG, "Invoking: getMovies");
        return movieDataRepository.fetchMainMenuMovieData();
    }

    public void applyNewSortByCriteria(SortByCriteria sortByCriteria){
        movieDataRepository.applyLookupBySortByCriteria(sortByCriteria);
    }
}
