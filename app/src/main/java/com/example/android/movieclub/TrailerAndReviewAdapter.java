package com.example.android.movieclub;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 09/01/2017.
 */

public class TrailerAndReviewAdapter extends RecyclerView.Adapter<TrailerAndReviewAdapter.TrailerAndReviewViewHolder>
{
    private static final String TAG = TrailerAndReviewAdapter.class.getSimpleName();

    MovieItemClickListener mOnClickListener;
    List<String> mNames;
    List<Boolean> mIsTrailer;
    Context mContext;

    // Interface to Handle item clicks
    public interface MovieItemClickListener
    {
        void onMovieItemClick(int itemIndex);
    }

    public TrailerAndReviewAdapter(MovieItemClickListener movieItemClickListener, Context context)
    {
        mOnClickListener = movieItemClickListener;
        mNames = new ArrayList<>();
        mIsTrailer = new ArrayList<>();
        mContext = context;
    }

    String getMovieData(int position)
    {
        if(mNames != null && mNames.size() > position)
        {
            return mNames.get(position);
        }

        return null;
    }

    void setNames(List<String> names, List<Boolean> isTrailer)
    {
        mNames = names;
        mIsTrailer =  isTrailer;

        notifyDataSetChanged();
    }

    @Override
    public TrailerAndReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.trailer_review_item, parent, false);

        TrailerAndReviewViewHolder viewHolder = new TrailerAndReviewViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerAndReviewViewHolder holder, int position)
    {
        holder.mName.setText(mNames.get(position));

        if(mIsTrailer.get(position))
        {
            holder.mImage.setImageResource(android.R.drawable.ic_media_play);
        }

        else
        {
            holder.mImage.setImageResource(android.R.drawable.ic_menu_view);
        }
    }

    @Override
    public int getItemCount()
    {
        if(mNames != null)
        {
            return mNames.size();
        }

        return 0;
    }

    /**
     * Cache of the children views for a list item
     */
    class TrailerAndReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView mName;
        ImageView mImage;
        boolean mIsTrailer;

        public TrailerAndReviewViewHolder(View itemView)
        {
            super(itemView);

            mName = (TextView) itemView.findViewById(R.id.tv_name);
            mImage = (ImageView) itemView.findViewById(R.id.iv_trailer_review);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            int clickedPosition = getAdapterPosition();

            mOnClickListener.onMovieItemClick(clickedPosition);
        }
    }
}
