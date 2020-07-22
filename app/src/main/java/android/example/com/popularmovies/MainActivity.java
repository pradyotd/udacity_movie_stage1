package android.example.com.popularmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO: Should we be fetching the data from movie db at start up or as required?
//TODO: Resolve issues with posters displaying on the main menu
//TODO: Determine number of pages of data to fetch from the API.


//By default we will display movies sorted popularity.
//You can use the options menu to change that.
public class MainActivity extends AppCompatActivity implements MovieMenuAdapter.MovieMenuAdapterOnClickHandler {

    private final static String TAG = MainActivity.class.getName();

    private Map<SortByCriteria, List<MovieData>> mMovieDataSortedByCriteriaMap;

    private final static int NUM_COLUMNS = 2;

    private RecyclerView mRecyclerView;
    private MovieMenuAdapter mMovieMenuAdapter;
    private URLManager mUrlManager;


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemSelected = item.getItemId();
        if (itemSelected == R.id.action_sort_by_pop) {
            //Do we have the data? if so set it on the adapter and invoke notify
            if (!mMovieDataSortedByCriteriaMap.containsKey(SortByCriteria.POPULARITY)) {
                loadMovieData(SortByCriteria.POPULARITY);
            } else {
                //Since we already have the data. All we need to do is set the data on the adapter and.
                loadDataToAdapter(mMovieDataSortedByCriteriaMap.get(SortByCriteria.POPULARITY));
            }

        } else if (itemSelected == R.id.action_sort_by_rate) {
            if (!mMovieDataSortedByCriteriaMap.containsKey(SortByCriteria.TOP_RATED)) {
                loadMovieData(SortByCriteria.TOP_RATED);
            } else {
                //Since we already have the data. All we need to do is set the data on the adapter and.
                //notify.
                loadDataToAdapter(mMovieDataSortedByCriteriaMap.get(SortByCriteria.TOP_RATED));
            }
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

        mRecyclerView = findViewById(R.id.rvMainMovieMenu);
        //TODO: Maybe controls to display errors.
        //TODO: We might need to add a progress bar.
        GridLayoutManager layoutManager = new GridLayoutManager(this, NUM_COLUMNS);
        mRecyclerView.setLayoutManager(layoutManager);
        //We really want all the children to be the same size in the main menu
        mRecyclerView.setHasFixedSize(true);
        ;
        mUrlManager = new URLManager(this.getString(R.string.MOVIEDB_API_KEY), this.getString(R.string.BASE_LOOKUP_URL), this.getString(R.string.BASE_PHOTO_URL));
        mMovieMenuAdapter = new MovieMenuAdapter(mUrlManager, "w185", this);
        mRecyclerView.setAdapter(mMovieMenuAdapter);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.densityDpi;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.densityDpi;

        Log.i(TAG, String.format("dpi height: %f dpi width: %f ", dpHeight, dpWidth));
        mMovieDataSortedByCriteriaMap = new HashMap<>();

        //loadMovieData(SortByCriteria.POPULARITY);
        checkInternetAndLoadData(SortByCriteria.POPULARITY);

    }

    private void checkInternetAndLoadData(SortByCriteria sortByCriteria){
        new CheckInternetTask(this, sortByCriteria).execute();
    }

    private void loadMovieData(SortByCriteria sortByCriteria) {
        //TODO: we probably want to check to see if we already have the data for
        //the sortcriteria
        new MovieDataFetchTask(sortByCriteria).execute();
    }

    @Override
    public void onClickHandler(MovieData movieData) {
        Context context = this;
        Class destinationClass = MovieDetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        // COMPLETED (1) Pass the weather to the DetailActivity
        Log.v(TAG, movieData.toString());
        intentToStartDetailActivity.putExtra("MovieDetail", new MovieDataParcel(movieData));
        startActivity(intentToStartDetailActivity);
    }

    public static class CheckInternetTask extends AsyncTask<Void, Void, Boolean> {

        private final MainActivity activity;
        private final SortByCriteria msortByCriteria;

        public CheckInternetTask(MainActivity activity, SortByCriteria sortByCriteria) {
            this.activity = activity;
            this.msortByCriteria = sortByCriteria;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Socket sock = new Socket();
                sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
                sock.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                this.activity.loadMovieData(this.msortByCriteria);
            } else {
                Toast toast = Toast.makeText(this.activity, "Pls check your internet connection", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }


    public class MovieDataFetchTask extends AsyncTask<Void, Void, List<MovieData>> {
        private final SortByCriteria mSortByCriteria;

        public MovieDataFetchTask(SortByCriteria mSortByCriteria) {
            this.mSortByCriteria = mSortByCriteria;
        }

        @Override
        protected List<MovieData> doInBackground(Void... params) {

            URL movieURL = mUrlManager.getPopularityUrl();
            if (mSortByCriteria == SortByCriteria.TOP_RATED) {
                movieURL = mUrlManager.getTopRatedUrl();
            }
            Log.v(TAG, "Using URL: " + movieURL);
            try {
                String movieDBResponse = NetworkUtils.getResponseFromHttpUrl(movieURL);

                Gson gson = new Gson();
                Response response = gson.fromJson(movieDBResponse, Response.class);
                Log.v(TAG, String.format("Response Data: page: %d total pages: %d", response.getPage(), response.getTotal_pages()));
                List<MovieData> movieData = Arrays.asList(response.getResults());
                Log.v(TAG, String.format("Obtained Movie Data: %d", movieData.size()));
                return movieData;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Collections.emptyList();
        }

        @Override
        protected void onPostExecute(List<MovieData> movieData) {
            Log.v(TAG, "onPostExecute: Setting movieData on Adapter");
            mMovieDataSortedByCriteriaMap.put(mSortByCriteria, movieData);
            loadDataToAdapter(movieData);
        }
    }

    private void loadDataToAdapter(List<MovieData> movieData) {
        mMovieMenuAdapter.setMovieData(movieData);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}