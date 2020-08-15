package android.example.com.popularmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;


//TODO: Determine number of pages of data to fetch from the API.


//By default we will display movies sorted popularity.
//You can use the options menu to change that.
public class MainActivity extends AppCompatActivity implements MovieMenuAdapter.MovieMenuAdapterOnClickHandler {

    private final static String TAG = MainActivity.class.getName();

    private final static int NUM_COLUMNS = 2;

    private RecyclerView mRecyclerView;
    private MovieMenuAdapter mMovieMenuAdapter;
    private MainViewModel model;
    private AppDatabase mDb;
    private TextView mNoDataCaption;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemSelected = item.getItemId();

        if (itemSelected == R.id.action_sort_by_pop) {
            Log.i(TAG, "SORT BY POPULARITY");
            model.applyNewSortByCriteria(SortByCriteria.POPULARITY);
        } else if (itemSelected == R.id.action_sort_by_rate) {
            Log.i(TAG, "SORT BY HIGHEST RATED");
            model.applyNewSortByCriteria(SortByCriteria.TOP_RATED);
        } else if (itemSelected == R.id.action_sort_by_favorite){
            Log.i(TAG, "Order byb Favorites");
            model.applyNewSortByCriteria(SortByCriteria.FAVORITES);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNoDataCaption = findViewById(R.id.tvNoDataMessage);
        mRecyclerView = findViewById(R.id.rvMainMovieMenu);
        //TODO: Maybe controls to display errors.
        //TODO: We might need to add a progress bar.
        GridLayoutManager layoutManager = new GridLayoutManager(this, NUM_COLUMNS);
        mRecyclerView.setLayoutManager(layoutManager);
        //We really want all the children to be the same size in the main menu
        mRecyclerView.setHasFixedSize(true);

        mMovieMenuAdapter = new MovieMenuAdapter( this);
        mRecyclerView.setAdapter(mMovieMenuAdapter);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.densityDpi;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.densityDpi;

        Log.i(TAG, String.format("dpi height: %f dpi width: %f ", dpHeight, dpWidth));
        mDb = AppDatabase.getInstance(getApplicationContext());
        setupViewModel();
        //checkInternetAndLoadData(SortByCriteria.POPULARITY);

    }

    private void setupViewModel() {
        model = ViewModelProviders.of(this).get(MainViewModel.class);
        Log.i(TAG, "Setting up ViewModel");
        //The expectation here is that we do getMovies with a default of SortByPopularity
        //We however will need to change this based on
        model.getMovies().observe(this, new Observer<List<MovieData>>() {
            @Override
            public void onChanged(List<MovieData> movieData) {
                Log.i(TAG, "Loading Data to adapter");
                loadDataToAdapter(movieData);
            }
        });
    }


    @Override
    public void onClickHandler(int movieId) {
        Log.i(TAG,"Handling Click For: " + movieId);
        Context context = this;
        Class destinationClass = MovieDetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("MovieId", movieId);
        startActivity(intentToStartDetailActivity);
    }

    private void loadDataToAdapter(List<MovieData> movieData) {
        if (movieData.isEmpty()){
            mNoDataCaption.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            mNoDataCaption.setVisibility(View.INVISIBLE);
            mMovieMenuAdapter.setMovieData(movieData);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

    }
}