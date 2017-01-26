package com.example.android.movieclub;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Daniel on 25/01/2017.
 */

public class FavoriteProvider extends ContentProvider
{
    public static final int CODE_FAVORITES = 100;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DBHelper mOpenHelper;

    @Override
    public boolean onCreate()
    {
        mOpenHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        Cursor cursor;

        switch (sUriMatcher.match(uri))
        {
            /*case CODE_WEATHER_WITH_DATE:
            {
                String normalizedUtcDateString = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{normalizedUtcDateString};

                cursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        WeatherContract.WeatherEntry.COLUMN_DATE + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;
            }*/

            case CODE_FAVORITES:
            {
                cursor = mOpenHelper.getReadableDatabase().query(FavoriteContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri))
        {
            case CODE_FAVORITES:
                db.beginTransaction();
                int rowsInserted = 0;
                try
                {
                    for (ContentValues value : values)
                    {
                        long _id = db.insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null, value);

                        if (_id != -1)
                        {
                            rowsInserted++;
                        }
                    }

                    db.setTransactionSuccessful();
                }

                finally
                {
                    db.endTransaction();
                }

                if (rowsInserted > 0)
                {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri)
    {
        throw new RuntimeException("Did not implement this in Popular Movies");
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        throw new RuntimeException("Did not implement this in Popular Movies. Used bulkInsert instead");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        int numRowsDeleted;

        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri))
        {
            case CODE_FAVORITES:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(FavoriteContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRowsDeleted != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        throw new RuntimeException("Did not implement this in Popular Movies");
    }

    public static UriMatcher buildUriMatcher()
    {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoriteContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, FavoriteContract.PATH_FAVORITES, CODE_FAVORITES);

        return matcher;
    }
}
