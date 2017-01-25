package com.example.android.movieclub;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Daniel on 06/01/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>
{
    private static final String TAG = MainActivity.class.getSimpleName();

    MovieItemClickListener mOnClickListener;
    private List<MovieData> mMovieData;
    Context context;

    // Interface to Handle item clicks
    public interface MovieItemClickListener
    {
        void onMovieItemClick(int itemIndex);
    }

    public MovieAdapter(Context context, MovieItemClickListener movieItemClickListener)
    {
        this.context = context;
        mOnClickListener = movieItemClickListener;
    }

    MovieData getMovieData(int position)
    {
        if(mMovieData != null && mMovieData.size() > position)
        {
            return mMovieData.get(position);
        }

        return null;
    }

    void setMovieData(List<MovieData> movieData)
    {
        mMovieData = movieData;
        notifyDataSetChanged();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.movie_list_item, parent, false);

        MovieViewHolder viewHolder = new MovieViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position)
    {
        holder.movie.mMovieName.setText(mMovieData.get(position).getTitle());

        String url = "http://image.tmdb.org/t/p/" + "w500" + mMovieData.get(position).getPosterPath();
        Picasso.with(context).load(url).into(holder.movie.mMovieImage);
    }

    @Override
    public int getItemCount()
    {
        if(mMovieData != null)
        {
            return mMovieData.size();
        }

        return 0;
    }

    /**
     * Cache of the children views for a list item
     */
    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        Movie movie;

        public MovieViewHolder(View itemView)
        {
            super(itemView);

            movie = new Movie(itemView, R.id.iv_movie_thumb, R.id.tv_movie_name);
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
