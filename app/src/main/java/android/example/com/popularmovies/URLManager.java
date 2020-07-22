package android.example.com.popularmovies;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;



public class URLManager {
    private static final String TAG = URLManager.class.getSimpleName();

    private final String apiKey;
    private final String baseUrlPhoto;
    private final URL popularityUrl;
    private final URL topRatedUrl;


    public URLManager(String apiKey, String baseUrl, String baseUrlPhoto) {
        this.apiKey = apiKey;

        this.baseUrlPhoto = baseUrlPhoto;
        this.popularityUrl = constructUrl("popular",  baseUrl);
        this.topRatedUrl = constructUrl("top_rated",  baseUrl);
    }
    //TODO: Take out all references to the api key before commit.


    private URL constructUrl(String sortCriteria, String baseUrlApi){
        Uri.Builder builder = new Uri.Builder();
        Uri builtUri =  builder.scheme("http")
                .authority(baseUrlApi)
                .appendPath("3") //API Version
                .appendPath("movie")
                .appendPath(sortCriteria)
                .appendQueryParameter("api_key", this.apiKey).build();

        return convertUriToUrl(builtUri);
    }

    public URL getPopularityUrl() {
        return popularityUrl;
    }

    public URL getTopRatedUrl() {
        return topRatedUrl;
    }

    private URL convertUriToUrl(Uri builtUri) {
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);
        return url;
    }

    public Uri getPhotoURI(String photoSize, String photoExtension){
        Uri.Builder builder = new Uri.Builder();
        return builder.scheme("http")
                .authority(this.baseUrlPhoto)
                .appendPath("t")
                .appendPath("p")
                .appendPath(photoSize)
                .appendPath(photoExtension).build();

    }


}
