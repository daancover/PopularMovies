package com.example.android.movieclub;

import android.provider.BaseColumns;

/**
 * Created by Daniel on 10/01/2017.
 */

public class FavoritesDB implements BaseColumns
{
    public static final String TABLE_NAME = "favorites";
    public static final String POSTER = "poster_path";
    public static final String OVERVIEW = "overview";
    public static final String RELEASE_DATE = "release_date";
    public static final String TITLE = "original_title";
    public static final String VOTE_AVERAGE = "vote_average";
    public static final String ID = "id";

    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ",";
    public static final String CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    POSTER + TEXT_TYPE + COMMA_SEP +
                    OVERVIEW + TEXT_TYPE + COMMA_SEP +
                    RELEASE_DATE + TEXT_TYPE + COMMA_SEP +
                    TITLE + TEXT_TYPE + COMMA_SEP +
                    VOTE_AVERAGE + TEXT_TYPE + COMMA_SEP +
                    ID + TEXT_TYPE + " )";

    public static final String DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public FavoritesDB() {}
}
