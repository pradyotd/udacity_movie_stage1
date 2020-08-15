package android.example.com.popularmovies;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;




import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {
    private final static String TAG = TrailerAdapter.class.getName();
    private List<Video> mTrailerData;
    private final TrailerAdapter.TrailerAdapterOnClickHandler mOnClickHandler;

    interface TrailerAdapterOnClickHandler {
        void onClickHandler(Video video);
    }

    public TrailerAdapter(TrailerAdapterOnClickHandler mOnClickHandler) {
        this.mOnClickHandler = mOnClickHandler;
    }


    @NonNull
    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutForTrailerItem = R.layout.trailer_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutForTrailerItem, parent ,shouldAttachToParentImmediately );

        return new TrailerAdapter.TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapterViewHolder holder, int position) {
        holder.mTrailerItem = mTrailerData.get(position);
        holder.mPlayButton.setText(holder.mTrailerItem.getName());
        Log.v(TAG, "Binding Trailer:" + holder.mTrailerItem.getName());
    }

    @Override
    public int getItemCount() {
        if (mTrailerData == null || mTrailerData.isEmpty()) return 0;
        return mTrailerData.size();
    }

    public void setTrailerData(List<Video> mTrailerData) {
        this.mTrailerData = mTrailerData;
        notifyDataSetChanged();
    }



    public class TrailerAdapterViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener {

        //TODO: Should this be private?
        public final Button mPlayButton;
        public Video mTrailerItem;

        @Override
        public void onClick(View view) {
            Log.i(TAG, "Adapter clicked");
            int adapterPosition = getAdapterPosition();
            mOnClickHandler.onClickHandler(mTrailerData.get(adapterPosition));
        }

        public TrailerAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mPlayButton = itemView.findViewById(R.id.btnTrailer);
            itemView.setOnClickListener(this);
            mPlayButton.setOnClickListener(this);
        }
    }

}
