package com.example.android.movieclub;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Daniel on 06/01/2017.
 */

public class MovieData implements Parcelable
{
    public static final String EXTRA_MOVIE_DATA = "com.example.android.movieclub.MovieData";

    private String posterPath;
    private String releaseDate;
    private String title;
    private String overview;
    private String voteAverage;
    private String id;

    public MovieData(Parcel in)
    {
        this.posterPath = in.readString();
        this.releaseDate = in.readString();
        this.title = in.readString();
        this.overview = in.readString();
        this.voteAverage = in.readString();
        this.id = in.readString();
    }

    public MovieData(String posterPath, String releaseDate, String title, String overview, String voteAverage, String id)
    {
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.title = title;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public String getPosterPath()
    {
        return posterPath;
    }

    public String getOverview()
    {
        return overview;
    }

    public String getTitle()
    {
        return title;
    }

    public String getReleaseDate()
    {
        return releaseDate;
    }

    public String getVoteAverage()
    {
        return voteAverage;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.posterPath);
        dest.writeString(this.releaseDate);
        dest.writeString(this.title);
        dest.writeString(this.overview);
        dest.writeString(this.voteAverage);
        dest.writeString(this.id);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };
}
