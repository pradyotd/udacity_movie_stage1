package android.example.com.popularmovies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {
    private final static String TAG = MovieDetailActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        TextView mTitle = findViewById(R.id.tv_detail_title);
        ImageView mPoster = findViewById(R.id.iv_detail_poster);
        TextView mReleaseDate = findViewById(R.id.tv_detail_release_date);
        TextView mVoteAvg = findViewById(R.id.tv_detail_vote_avg);
        TextView mOverview = findViewById(R.id.tv_detail_overview);

        Intent intentThatStartedActivity = getIntent();
        MovieDataParcel movieDataParcel =  intentThatStartedActivity.getParcelableExtra("MovieDetail");
        if (movieDataParcel != null) {
            Picasso.get().load(Uri.parse(movieDataParcel.getmPosterUriStr())).into(mPoster);
            String title = movieDataParcel.getmTitle();
            mTitle.setText(title);
            String release = movieDataParcel.getmReleaseDate();
            mReleaseDate.setText(release);
            String voteAvg = movieDataParcel.getmVoteAverage();
            mVoteAvg.setText(String.format("%s/10", voteAvg));
            String overview  = movieDataParcel.getmOverview();
            mOverview.setText(movieDataParcel.getmOverview());
            Log.v(TAG, String.format("Release: %s, VoteAvg: %s Overview: %s", release, voteAvg, overview));
        }
    }
}