package android.example.com.popularmovies;


import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class MovieMenuAdapter extends RecyclerView.Adapter<MovieMenuAdapter.MovieMenuAdapterViewHolder> {
    private final static String TAG = MovieMenuAdapter.class.getName();
    private List<MovieData>  mMovieData;
    private final URLManager mUrlManager;
    private final String mPhotoSize;
    private final MovieMenuAdapterOnClickHandler mOnClickHandler;
    private final int mItemHeight;
    private final int mItemWidth;

    interface MovieMenuAdapterOnClickHandler {
        void onClickHandler(MovieData movieData);
    }

    public MovieMenuAdapter(URLManager urlManager, String photoSize, MovieMenuAdapterOnClickHandler clickHandler) {
        mUrlManager = urlManager;
        mPhotoSize = photoSize;
        mOnClickHandler = clickHandler;
        mItemHeight =  Resources.getSystem().getDisplayMetrics().heightPixels/2;
        mItemWidth = Resources.getSystem().getDisplayMetrics().widthPixels/2;
    }

    public void setMovieData(List<MovieData> mMovieData) {
        this.mMovieData = mMovieData;
        notifyDataSetChanged();
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    @Override
    public MovieMenuAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutForMovieMenuItem = R.layout.movie_menu_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutForMovieMenuItem, parent ,shouldAttachToParentImmediately );
        view.getLayoutParams().height = mItemHeight;
        view.getLayoutParams().width = mItemWidth;
        return new MovieMenuAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieMenuAdapterViewHolder holder, int position) {

       holder.mMovieDataItem  = mMovieData.get(position);
       Log.v(TAG, "Binding Movie:" + holder.mMovieDataItem.getTitle() );
       String posterPath = holder.mMovieDataItem.getPoster_path().substring(1);
       Log.v(TAG, "Poster Path " + posterPath );
       Uri photoURI = mUrlManager.getPhotoURI(mPhotoSize, posterPath);
       holder.mMovieDataItem.setPosterUri(photoURI);

       Log.v(TAG, "Poster Uri " + photoURI.toString() );
       Picasso.get().load(photoURI).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        if (mMovieData == null) return 0;
        return mMovieData.size();
    }

    public class MovieMenuAdapterViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener {

        //TODO: Should this be private?
        public final ImageView mImageView;
        public MovieData mMovieDataItem;

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mOnClickHandler.onClickHandler(mMovieData.get(adapterPosition));
        }

        public MovieMenuAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imvMovieMenu);
            itemView.setOnClickListener(this);
        }
    }

}
