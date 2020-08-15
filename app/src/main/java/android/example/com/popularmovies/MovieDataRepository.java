package android.example.com.popularmovies;

import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDataRepository {
    //TODO: make sure this is removed before submitting.
    private final static String TAG = MovieDataRepository.class.getName();
    //private static final String API_KEY = "4c75f3c94c0dfa689b24de3b087c6180";
    private static MovieDataRepository movieDataRepository;
    private MovieWebService webService;
    private Map<Integer, List<Video>> mMovieVideoMap;
    private Map<Integer, List<Review>> mReviewMap;
    private Map<Integer, MovieData> movieDataMap;
    private Map<SortByCriteria, List<MovieData>> mMovieDataSortedByCriteriaMap;
    private final MutableLiveData<List<MovieData>> movieData;
    private final MutableLiveData<MovieData> mMovieDetailData;
    private final MutableLiveData<List<Review>> mMovieReviewData;
    //TODO: What we really need here are uri/urls.
    private final MutableLiveData<List<Video>> mMovieTrailerData;
    private String photoUrl;
    private AppDatabase mDb;
    private String apiKey;

    private MovieDataRepository(AppDatabase db, String apiKey) {
        Log.i(TAG, "Constructing MovieDataRepository");
        this.mDb = db;
        this.apiKey = apiKey;
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        this.photoUrl = "image.tmdb.org";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        this.webService = retrofit.create(MovieWebService.class);
        this.movieData = new MutableLiveData<>();
        this.mMovieDetailData = new MutableLiveData<>();
        this.mMovieReviewData = new MutableLiveData<>();
        this.mMovieTrailerData = new MutableLiveData<>();
        this.movieDataMap = new HashMap<>();
        this.mMovieDataSortedByCriteriaMap = new HashMap<>();
        this.mReviewMap = new HashMap<>();
        this.mMovieVideoMap = new HashMap<>();
    }

    public static MovieDataRepository getInstance(AppDatabase db, String apiKey) {
        Log.i(TAG, "getInstance");
        if (movieDataRepository == null){
            movieDataRepository = new MovieDataRepository(db, apiKey);
        }
        Log.i(TAG, "getInstance");
        return movieDataRepository;
    }

    private void lookupMovieData(SortByCriteria sortByCriteria){
        Log.i(TAG, "LookupMovieData " + sortByCriteria);
        if (sortByCriteria == SortByCriteria.POPULARITY) {
            getPopularMovies();
        } else if (sortByCriteria == SortByCriteria.TOP_RATED){
            getHighestRated();
        } else {
            //Lookup favorites.
            //Update Cache. Since this is where we look up details from.
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    List<MovieData> favorites  = lookupFavorites();
                    for (MovieData mv: favorites){
                        if (!movieDataMap.containsKey(mv.getId())){
                            movieDataMap.put(mv.getId(), mv);
                        }
                    }
                    movieData.postValue(favorites);
                }
            });
        }
    }

    private void processReviewResponse(int movieId, List<Review> reviews){
        mReviewMap.put(movieId, reviews);
        mMovieReviewData.setValue(reviews);
    }

    private void processTrailersResponse(int movieId, List<Video> videos){
        mMovieVideoMap.put(movieId, videos);
        mMovieTrailerData.setValue(videos);
    }

    private void processMovieDataResponse(SortByCriteria sortByCriteria, List<MovieData> movieDataList) {
        //We need to set the photoUri.
        Log.i(TAG, "processMovieDataResponse found :" + movieDataList.size());
        if (!movieDataList.isEmpty()) {
            for (MovieData mv: movieDataList){

                Uri uri = getPhotoURI(mv.getPoster_path().substring(1));
                Log.i(TAG, "processMovieDataResponse Uri: " + uri);
                mv.setPosterUri(uri);
            }

            mMovieDataSortedByCriteriaMap.put(sortByCriteria, movieDataList);
            movieDataList.forEach(m -> movieDataMap.put(m.getId(), m));
            movieData.setValue(mMovieDataSortedByCriteriaMap.get(sortByCriteria));
        } else {
            Log.w(TAG, "Could not lookupMovieData");
        }
    }

    public void applyLookupBySortByCriteria(SortByCriteria sortByCriteria){
        Log.i(TAG, "applyLookupBySortByCriteria: lookupMovieData: " + sortByCriteria);
        if (mMovieDataSortedByCriteriaMap.containsKey(sortByCriteria)){
            movieData.setValue(mMovieDataSortedByCriteriaMap.get(sortByCriteria));
            return;
        }
        lookupMovieData(sortByCriteria);
    }

    public LiveData<List<MovieData>> fetchMainMenuMovieData() {
        lookupMovieData(SortByCriteria.POPULARITY);
        return movieData;
    }

    public void getPopularMovies() {
        Log.i(TAG, "getPopularMovies");
        Call<MovieResult> callAsync = this.webService.getPopularMovies(this.apiKey);

        callAsync.enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                Log.i(TAG, "onResponse");
                MovieResult result = response.body();
                if (result != null && result.getResults() != null){
                    Log.i(TAG, "onResponse: Setting result: " + result.getResults().length);
                    processMovieDataResponse(SortByCriteria.POPULARITY, Arrays.asList(result.getResults()));
                } else {
                    Log.w(TAG, "No Response received");
                }
            }

            @Override
            public void onFailure(Call<MovieResult> call, Throwable throwable) {
                System.out.println(throwable);
            }
        });
    }

    public void getHighestRated() {
        Log.i(TAG, "getHighestRated");
        Call<MovieResult> callAsync = this.webService.getTopRatedMovies(this.apiKey);
        List<MovieData> highestRated = new ArrayList<>();
        callAsync.enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                MovieResult result = response.body();
                if (result != null){
                    processMovieDataResponse(SortByCriteria.TOP_RATED, Arrays.asList(result.getResults()));
                }
            }

            @Override
            public void onFailure(Call<MovieResult> call, Throwable throwable) {
                System.out.println(throwable);
            }
        });
    }

    public void getTrailers(int movieId) {

        Call<VideoResult> callAsync = this.webService.getTrailers(movieId, this.apiKey);

        callAsync.enqueue(new Callback<VideoResult>() {
            @Override
            public void onResponse(Call<VideoResult> call, Response<VideoResult> response) {
                VideoResult result = response.body();
                if (result != null){
                    processTrailersResponse(movieId, Arrays.asList(result.getResults()));
                }
            }

            @Override
            public void onFailure(Call<VideoResult> call, Throwable throwable) {
                System.out.println(throwable);
            }
        });
    }

    public void getReviews(int movieId) {
        Log.i(TAG, "getReviews for movie: " + movieId);
        Call<ReviewResult> callAsync = this.webService.getReviews(movieId, this.apiKey);

        callAsync.enqueue(new Callback<ReviewResult>() {
            @Override
            public void onResponse(Call<ReviewResult> call, Response<ReviewResult> response) {
                ReviewResult result = response.body();
                if (result != null){
                    Log.i(TAG, "getReviews Obtained results " + result.getResults().length);
                    processReviewResponse(movieId, Arrays.asList(result.getResults()));
                } else {
                    Log.i(TAG, "getReviews NO RESULTS Obtained");
                }
            }

            @Override
            public void onFailure(Call<ReviewResult> call, Throwable throwable) {
                System.out.println(throwable);
            }
        });

    }

    //Its unlikely that we will ever invoke this without having the moviedata already.
    //Since the only way we can invoke this is via the main menu.
    public LiveData<MovieData> fetchMovieData(int movieId) {
        if (movieDataMap.containsKey(movieId)){
            mMovieDetailData.setValue(movieDataMap.get(movieId));
        } else {
            Log.w(TAG, "Do not have the details for movieId:" + movieId);
        }
        return mMovieDetailData;
    }

    public LiveData<List<Review>> fetchReviews(int movieId) {
        Log.i(TAG, "Fetch Reviews");
        lookupReviews(movieId);
        return mMovieReviewData;
    }

    private void lookupReviews(int movieId) {
        if (mReviewMap.containsKey(movieId)){
            mMovieReviewData.setValue(mReviewMap.get(movieId));
            return;
        }
        getReviews(movieId);
    }

    private void lookupTrailers(int movieId){
        if (mMovieVideoMap.containsKey(movieId)){
            mMovieTrailerData.setValue(mMovieVideoMap.get(movieId));
            return;
        }
        getTrailers(movieId);
    }

    public LiveData<List<Video>> fetchTrailers(int movieId) {
        Log.i(TAG, "Fetch Trailers");
        lookupTrailers(movieId);
        return mMovieTrailerData;
    }

    private Uri getPhotoURI(String photoExtension){
        //TODO: Look into passing in the photosize.
        Log.i(TAG, "getPhotoUri: poster path: '" + photoExtension +"'");
        Uri.Builder builder = new Uri.Builder();
        return builder.scheme("http")
                .authority(this.photoUrl)
                .appendPath("t")
                .appendPath("p")
                .appendPath("w185")
                .appendPath(photoExtension).build();
    }

    public void createFavorite(int movieId){
        //We should clearly have the details.
        MovieData movieData = movieDataMap.get(movieId);
        assert (movieData != null);
        MovieDetails details = new MovieDetails(movieData);
        mDb.movieDao().insert(details);
    }

    public void deleteFavorite(int movieId){
        //We should clearly have the details.
        mDb.movieDao().deleteMovie(movieId);
    }

    public List<MovieData> lookupFavorites(){
        return Objects.requireNonNull(mDb.movieDao().getFavorites()).stream().map(MovieData::new).collect(Collectors.toList());

    }




}
