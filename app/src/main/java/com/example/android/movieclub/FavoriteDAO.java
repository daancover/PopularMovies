package com.example.android.movieclub;

/**
 * Created by Daniel on 10/01/2017.


public class FavoriteDAO
{
    private Context context;

    public FavoriteDAO(Context context)
    {
        this.context = context;
    }

    private SQLiteDatabase getDatabase()
    {
        DBHelper databaseHelper = new DBHelper(context);

        return databaseHelper.getWritableDatabase();
    }

    public void saveFavorite(MovieData movieData)
    {
        ContentValues values = new ContentValues();
        values.put(FavoritesDB.POSTER, movieData.getPosterPath());
        values.put(FavoritesDB.OVERVIEW, movieData.getOverview());
        values.put(FavoritesDB.RELEASE_DATE, movieData.getReleaseDate());
        values.put(FavoritesDB.TITLE, movieData.getTitle());
        values.put(FavoritesDB.VOTE_AVERAGE, movieData.getVoteAverage());
        values.put(FavoritesDB.ID, movieData.getId());

        getDatabase().insert(FavoritesDB.TABLE_NAME, null, values);
    }

    public List<MovieData> loadMovies()
    {
        List<MovieData> returnList = new ArrayList<>();

        String[] columns = { FavoritesDB._ID, FavoritesDB.POSTER, FavoritesDB.OVERVIEW, FavoritesDB.RELEASE_DATE, FavoritesDB.TITLE, FavoritesDB.VOTE_AVERAGE, FavoritesDB.ID };

        Cursor cursor = getDatabase().query(FavoritesDB.TABLE_NAME, columns, null, null, null, null, null);

        while(cursor.moveToNext())
        {
            MovieData movie = new MovieData(cursor.getString(cursor.getColumnIndex(FavoritesDB.POSTER)),
                cursor.getString(cursor.getColumnIndex(FavoritesDB.RELEASE_DATE)),
                cursor.getString(cursor.getColumnIndex(FavoritesDB.TITLE)),
                cursor.getString(cursor.getColumnIndex(FavoritesDB.OVERVIEW)),
                cursor.getString(cursor.getColumnIndex(FavoritesDB.VOTE_AVERAGE)),
                cursor.getString(cursor.getColumnIndex(FavoritesDB.ID)));

            returnList.add(movie);
        }

        cursor.close();

        return returnList;
    }

    public void deleteMovie(MovieData movieData)
    {
        String selection = FavoritesDB.TITLE + " = ?";

        String[] selectionArgs = {movieData.getTitle()};

        getDatabase().delete(FavoritesDB.TABLE_NAME, selection, selectionArgs);
    }

    public boolean exists(MovieData movieData)
    {
        String selection = FavoritesDB.TITLE + "=?";

        String[] selectionArgs = {movieData.getTitle()};

        String[] columns = { FavoritesDB.TITLE };

        Cursor cursor = getDatabase().query(FavoritesDB.TABLE_NAME, columns, selection, selectionArgs, null, null, null);

        //Log.d("DATABASE", cursor.getCount() + "");

        return cursor.moveToFirst();
    }
}*/