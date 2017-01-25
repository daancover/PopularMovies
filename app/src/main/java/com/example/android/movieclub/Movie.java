package com.example.android.movieclub;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Daniel on 06/01/2017.
 */

public class Movie
{
    ImageView mMovieImage;
    TextView mMovieName;

    public Movie(View view, int movieImage, int movieName)
    {
        mMovieImage = (ImageView) view.findViewById(movieImage);
        mMovieName = (TextView) view.findViewById(movieName);
    }
}
