package android.example.com.popularmovies;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


//TODO: Address layout issues.. 1.overlapping image and overview 2. missing title.
//TODO: Add support for playing youtube videos
//TODO: Add support for displaying reviews.
//TODO: Add support for data binding.
//TODO: How are we going to display reviews/youtube videos?




public class MovieDetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler{
    private final static String TAG = MovieDetailActivity.class.getName();
    private MovieDetailViewModel model;
    private TextView mTitle;
    private ImageView mPoster;
    private TextView mReleaseDate;
    private TextView mVoteAvg;
    private TextView mOverview;
    private CheckBox mFavorites;
    private TextView mReviews;
    private int currentMovieId;
    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;
    private RecyclerView mTrailersRecyclerViews;
    private TrailerAdapter mTrailerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Log.i(TAG, "onCreate: DetailActivity");
        mTitle = findViewById(R.id.tv_detail_title);
        mPoster = findViewById(R.id.iv_detail_poster);
        mReleaseDate = findViewById(R.id.tv_detail_release_date);
        mVoteAvg = findViewById(R.id.tv_detail_vote_avg);
        mOverview = findViewById(R.id.tv_detail_overview);
        mFavorites = findViewById(R.id.btn_favorite);
        mReviews = findViewById(R.id.tv_reviews);
        mTrailersRecyclerViews =  findViewById(R.id.rvTrailers);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mTrailersRecyclerViews.setLayoutManager(layoutManager);
        mTrailersRecyclerViews.setHasFixedSize(true);
        mTrailerAdapter = new TrailerAdapter(this);
        mTrailersRecyclerViews.setAdapter(mTrailerAdapter);

        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
        mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    //We have favorited.
                    Log.i(TAG, "Creating a favorite:" + currentMovieId);
                    AppExecutors.getInstance().diskIO().execute(() -> model.applyFavorite(currentMovieId));

                } else {
                    Log.i(TAG, "Deleting a favorite:" + currentMovieId);
                    AppExecutors.getInstance().diskIO().execute(() -> model.clearFavorite(currentMovieId));
                }
            }
        };
        //TODO: is this the right place to set up favorites listener?
        //TODO: How Can we change the appearance of the button when favorited/unfavorited.
        mFavorites.setOnCheckedChangeListener(mOnCheckedChangeListener);
        Intent intentThatStartedActivity = getIntent();
        currentMovieId = intentThatStartedActivity.getIntExtra("MovieId", 0);
        Log.i(TAG,"onCreate DetailActivity: "+ currentMovieId);
        if (currentMovieId > 0) {
            setupViewModel(currentMovieId);
        }
    }

    private void setupViewModel(int movieId) {
        model = ViewModelProviders.of(this).get(MovieDetailViewModel.class);
        Log.i(TAG, "Setting up ViewModel");
        //The expectation here is that we do getMovies with a default of SortByPopularity
        //We however will need to change this based on
        model.getMovieData(movieId).observe(this, new Observer<MovieData>() {
            @Override
            public void onChanged(MovieData movieData) {
                Picasso.get().load(movieData.getPosterUri()).into(mPoster);
                String title = movieData.getTitle();
                mTitle.setText(title);
                String release = movieData.getRelease_date();
                mReleaseDate.setText(release);
                String voteAvg = movieData.getVote_average();
                mVoteAvg.setText(String.format("%s/10", voteAvg));
                String overview  = movieData.getOverview();
                mOverview.setText(overview);
                Log.v(TAG, String.format("Release: %s, VoteAvg: %s Overview: %s", release, voteAvg, overview));
            }
        });

        model.getMovieIsFavorite(movieId).observe(this, new Observer<List<MovieDetails>>() {
            @Override
            public void onChanged(List<MovieDetails> movieDetails) {
                boolean isFavorite = !movieDetails.isEmpty();
                mFavorites.setOnCheckedChangeListener(null);
                mFavorites.setChecked(isFavorite);
                mFavorites.setOnCheckedChangeListener(mOnCheckedChangeListener);
            }
        });

        model.getReviews(movieId).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mReviews.setText(s);
            }
        });

        model.getTrailers(movieId).observe(this, new Observer<List<Video>>() {
            @Override
            public void onChanged(List<Video> videos) {
                mTrailerAdapter.setTrailerData(videos);
            }
        });
    }

    @Override
    public void onClickHandler(Video video) {
        Log.i(TAG,"Handling Click For Video: " + video.getName());
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video.getKey()));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + video.getKey()));
        try {
            this.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            this.startActivity(webIntent);
        }
    }
}