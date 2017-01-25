package com.example.android.movieclub;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements TrailerAndReviewAdapter.MovieItemClickListener
{
    private static final String TAG = DetailActivity.class.getSimpleName();

    private static final String MOVIES_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
    private static final String API_KEY = "your_api_key";

    private static final String KEY = "key";
    private static final String NAME = "name";
    private static final String URL = "url";
    private static final String AUTHOR = "author";

    private ImageView mMovieImage;
    private TextView mTitle;
    private TextView mReleaseDate;
    private TextView mUserRating;
    private TextView mVoteAverage;
    private TextView mOverview;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;

    private FavoriteDAO db;
    private boolean isFavorite;

    MovieData mMovieData;
    List<String> mUrls;
    List<String> mNames;
    List<Boolean> mIsTrailer;
    TrailerAndReviewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = this.getSupportActionBar();

        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent().hasExtra(MovieData.EXTRA_MOVIE_DATA))
        {
            mProgressBar = (ProgressBar) findViewById(R.id.pb_progress_bar);

            mMovieData = getIntent().getParcelableExtra(MovieData.EXTRA_MOVIE_DATA);

            mMovieImage = (ImageView) findViewById(R.id.iv_movie_image);
            String url = "http://image.tmdb.org/t/p/" + "w500" + mMovieData.getPosterPath();
            Picasso.with(this).load(url).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(mMovieImage);

            mTitle = (TextView) findViewById(R.id.tv_title);
            mTitle.setText(mMovieData.getTitle());

            mReleaseDate = (TextView) findViewById(R.id.tv_release_date);
            mReleaseDate.setText(mMovieData.getReleaseDate());

            mUserRating = (TextView) findViewById(R.id.tv_user_rating);

            mVoteAverage = (TextView) findViewById(R.id.tv_vote_average);
            mVoteAverage.setText(mMovieData.getVoteAverage());

            mOverview = (TextView) findViewById(R.id.tv_overview);
            mOverview.setText(mMovieData.getOverview());

            mNames = new ArrayList<>();
            mUrls = new ArrayList<>();
            mIsTrailer = new ArrayList<>();

            mAdapter = new TrailerAndReviewAdapter(this, this);
            mRecyclerView = (RecyclerView) findViewById(R.id.rv_trailers);
            LinearLayoutManager trailerLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

            mRecyclerView.setLayoutManager(trailerLinearLayoutManager);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setAdapter(mAdapter);

            db = new FavoriteDAO(DetailActivity.this);
            isFavorite = db.exists(mMovieData);
            Log.d(TAG, "FAVORITE " + isFavorite);

            hideMovieDataView();
            loadMovieTrailers();
        }
    }

    void loadMovieTrailers()
    {
        new FetchTrailersAndReviewsTask().execute();
    }

    private void showMovieDataView()
    {
        mMovieImage.setVisibility(View.VISIBLE);
        mTitle.setVisibility(View.VISIBLE);
        mReleaseDate.setVisibility(View.VISIBLE);
        mUserRating.setVisibility(View.VISIBLE);
        mVoteAverage.setVisibility(View.VISIBLE);
        mOverview.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void hideMovieDataView()
    {
        mMovieImage.setVisibility(View.INVISIBLE);
        mTitle.setVisibility(View.INVISIBLE);
        mReleaseDate.setVisibility(View.INVISIBLE);
        mUserRating.setVisibility(View.INVISIBLE);
        mVoteAverage.setVisibility(View.INVISIBLE);
        mOverview.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMovieItemClick(int itemIndex)
    {
        if(mIsTrailer.get(itemIndex))
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_BASE_URL + mUrls.get(itemIndex)));

            if (intent.resolveActivity(getPackageManager()) != null)
            {
                startActivity(intent);
            }
        }

        else
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrls.get(itemIndex)));

            if (intent.resolveActivity(getPackageManager()) != null)
            {
                startActivity(intent);
            }
        }
    }

    public class FetchTrailersAndReviewsTask extends AsyncTask<String, Void, List<String>>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<String> doInBackground(final String... params)
        {
            final RequestQueue requestQueue = Volley.newRequestQueue(DetailActivity.this);
            URL trailersUrl = buildUrl("/videos");

            JsonObjectRequest jsonTrailersRequest = new JsonObjectRequest(Request.Method.GET, trailersUrl.toString(), new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response)
                {
                    try
                    {
                        // Split the movie attributes in an array
                        JSONArray jsonArray = response.getJSONArray("results");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            // Store all movie data
                            mUrls.add(object.getString(KEY));
                            mNames.add(object.getString(NAME));
                            mIsTrailer.add(true);
                        }

                    }

                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(DetailActivity.this, getString(R.string.json_exception), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Toast.makeText(DetailActivity.this, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error Response");
                }
            });

            requestQueue.add(jsonTrailersRequest);

            URL reviewsUrl = buildUrl("/reviews");

            JsonObjectRequest jsonReviewsRequest = new JsonObjectRequest(Request.Method.GET, reviewsUrl.toString(), new Response.Listener<JSONObject>()
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
                            mUrls.add(object.getString(URL));
                            mNames.add(object.getString(AUTHOR));
                            mIsTrailer.add(false);
                        }

                        mAdapter.setNames(mNames, mIsTrailer);
                        mProgressBar.setVisibility(View.GONE);
                        showMovieDataView();
                    }

                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(DetailActivity.this, getString(R.string.json_exception), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Toast.makeText(DetailActivity.this, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error Response");
                }
            });

            requestQueue.add(jsonReviewsRequest);

            return mUrls;
        }

        @Override
        protected void onPostExecute(List<String> movies)
        {
            super.onPostExecute(movies);
        }
    }

    public URL buildUrl(String vidOrRev)
    {
        Uri builtUri = Uri.parse(MOVIES_BASE_URL + mMovieData.getId() + vidOrRev + "?api_key=" + API_KEY).buildUpon().build();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.action_favorite)
        {
            if(isFavorite)
            {
                db.deleteMovie(mMovieData);
                isFavorite = false;
                Toast.makeText(this, getString(R.string.removed_favorite), Toast.LENGTH_SHORT).show();
            }

            else
            {
                db.saveFavorite(mMovieData);
                isFavorite = true;
                Toast.makeText(this, getString(R.string.added_favorite), Toast.LENGTH_SHORT).show();
            }
        }

        else if(id == android.R.id.home)
        {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }
}
