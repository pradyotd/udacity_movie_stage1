package android.example.com.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieDataParcel implements Parcelable {

    private String mTitle;
    private String mReleaseDate;
    private String mVoteAverage;
    private String mOverview;

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public void setmReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public String getmVoteAverage() {
        return mVoteAverage;
    }

    public void setmVoteAverage(String mVoteAverage) {
        this.mVoteAverage = mVoteAverage;
    }

    public String getmOverview() {
        return mOverview;
    }

    public void setmOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public String getmPosterUriStr() {
        return mPosterUriStr;
    }

    public void setmPosterUriStr(String mPosterUriStr) {
        this.mPosterUriStr = mPosterUriStr;
    }

    private String mPosterUriStr;

    public MovieDataParcel(MovieData mMovieData) {
        this.mTitle = mMovieData.getTitle();
        this.mReleaseDate = mMovieData.getRelease_date();
        this.mVoteAverage = mMovieData.getVote_average();
        this.mOverview = mMovieData.getOverview();
        this.mPosterUriStr = mMovieData.getPosterUri().toString();
    }

    protected MovieDataParcel(Parcel in) {
        this.mTitle = in.readString();
        this.mReleaseDate = in.readString();
        this.mVoteAverage = in.readString();
        this.mOverview = in.readString();
        this.mPosterUriStr = in.readString();

    }

    public static final Creator<MovieDataParcel> CREATOR = new Creator<MovieDataParcel>() {
        @Override
        public MovieDataParcel createFromParcel(Parcel in) {
            return new MovieDataParcel(in);
        }

        @Override
        public MovieDataParcel[] newArray(int size) {
            return new MovieDataParcel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mReleaseDate);
        dest.writeString(mVoteAverage);
        dest.writeString(mOverview);
        dest.writeString(mPosterUriStr);
    }
}
