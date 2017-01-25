package com.example.android.movieclub;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieItemClickListener
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int NUM_COLUMNS = 2;

    private static final String MOVIES_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String API_KEY = "your_api_key";
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final String FAVORITES = "favorites";

    private static final String POSTER = "poster_path";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE = "release_date";
    private static final String TITLE = "original_title";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String ID = "id";

    private static final String SORT_TYPE = "sort_type";

    private MovieAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private String mSortType;
    private FavoriteDAO db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_progress_bar);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, NUM_COLUMNS, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MovieAdapter(MainActivity.this, this);

        mRecyclerView.setAdapter(mAdapter);

        Log.d(TAG, "BEFORE " + mSortType);

        if(savedInstanceState == null)
        {
            // Check if user already selected a sort type
            if(mSortType == null || mSortType.equals(""))
            {
                mSortType = POPULAR;
            }
        }

        else
        {
            if(savedInstanceState.containsKey(SORT_TYPE))
            {
                mSortType = savedInstanceState.getString(SORT_TYPE);
            }
        }

        Log.d(TAG, "ON CREATE" + mSortType);

        loadMovieData(mSortType);
    }

    @Override
    public void onMovieItemClick(int itemIndex)
    {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(MovieData.EXTRA_MOVIE_DATA, mAdapter.getMovieData(itemIndex));
        startActivity(intent);
    }

    void loadMovieData(String sortType)
    {
        showMovieDataView();
        new FetchMovieTask().execute(sortType);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(SORT_TYPE, mSortType);
    }

    private void showMovieDataView()
    {
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public URL buildUrl(String sortType)
    {
        Uri builtUri = Uri.parse(MOVIES_BASE_URL + sortType + "?api_key=" + API_KEY).buildUpon().build();

        URL url = null;

        try
        {
            url = new URL(builtUri.toString());
        }

        catch (MalformedURLException e)
        {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.url_exception), Toast.LENGTH_SHORT).show();
        }

        return url;
    }

    public class FetchMovieTask extends AsyncTask<String, Void, List<MovieData>>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<MovieData> doInBackground(String... params)
        {
            if (params.length == 0)
            {
                Log.e(TAG, "PARAMS LENGTH EQUALS ZERO.");
                return null;
            }

            if(params[0].equals(FAVORITES))
            {
                db = new FavoriteDAO(MainActivity.this);

                final List<MovieData> movies = db.loadMovies();

                Log.d(TAG, "FAVORITES");

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mAdapter.setMovieData(movies);
                        showMovieDataView();
                    }
                });
            }

            else if(params[0].equals(POPULAR) || params[0].equals(TOP_RATED))
            {
                final List<MovieData> returnList = new ArrayList<>();

                final RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                String sortType = params[0];
                URL url = buildUrl(sortType);

                JsonObjectRequest jsonRequest =  new JsonObjectRequest(Request.Method.GET, url.toString(), new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            // Split the movie attributes in an array
                            JSONArray jsonArray = response.getJSONArray("results");

                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject object = jsonArray.getJSONObject(i);

                                // Store all movie data
                                final MovieData movieData = new MovieData(
                                        object.getString(POSTER),
                                        object.getString(RELEASE_DATE),
                                        object.getString(TITLE),
                                        object.getString(OVERVIEW),
                                        object.getString(VOTE_AVERAGE),
                                        object.getString(ID));

                                returnList.add(movieData);
                            }

                            mAdapter.setMovieData(returnList);
                            showMovieDataView();
                        }

                        catch(JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, getString(R.string.json_exception), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error Response");
                    }
                });

                    requestQueue.add(jsonRequest);
                    return returnList;
                }

            return null;
        }

        @Override
        protected void onPostExecute(List<MovieData> movies)
        {
            super.onPostExecute(movies);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Change movie sort type
        if(item.getItemId() == R.id.action_popular)
        {
            mAdapter.setMovieData(null);
            loadMovieData(POPULAR);
            mSortType = POPULAR;
        }

        else if(item.getItemId() == R.id.action_top_rated)
        {
            mAdapter.setMovieData(null);
            loadMovieData(TOP_RATED);
            mSortType = TOP_RATED;
        }

        else if(item.getItemId() == R.id.action_favorite)
        {
            mAdapter.setMovieData(null);
            loadMovieData(FAVORITES);
            mSortType = FAVORITES;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        /*mAdapter.setMovieData(null);
        loadMovieData(mSortType);*/
    }
}
