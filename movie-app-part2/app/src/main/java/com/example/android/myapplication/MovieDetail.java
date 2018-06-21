package com.example.android.myapplication;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.myapplication.Data.MoviesProvider;
import com.example.android.myapplication.Model.MovieClass;
import com.example.android.myapplication.Model.Review;
import com.example.android.myapplication.Model.Trailers;
import com.example.android.myapplication.utilities.JsonUtil;
import com.example.android.myapplication.utilities.NetworkCheck;
import com.example.android.myapplication.utilities.NetworkUtils;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.android.myapplication.R.drawable.baseline_thumb_down_black_18dp;
import static com.example.android.myapplication.R.drawable.baseline_thumb_up_black_18dp;



public class MovieDetail extends AppCompatActivity {

    //for movies and trailers key
    private static final String MOVIES_TRAILERS = "moviesTrailers";
    private static final String MOVIES_REVIEWS = "moviesReview";

    //for saving recyclerView scroll position
    private static final String SCROLLED_POSITION = "scrolled_position";

    //Declare ImageView to hold image and TextViews to hold texts
    private ImageView movieImage;
    private TextView titleText;
    private TextView rating;
    private TextView releaseDate;
    private MovieClass movieDetail;
    private ExpandableTextView expandtxt;
    private int movieId;
    private ArrayList<Review> reviews;
    private ArrayList<Trailers> trailers;
    private RecyclerView rvReviews;
    private reviewAdapter mAdapter;
    private RecyclerView rvTrailers;
    private TrailersAdapter trailersAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        setTitle("");

        movieImage = findViewById(R.id.detail_img);
        titleText = findViewById(R.id.title);
        rating = findViewById(R.id.rating);
        releaseDate = findViewById(R.id.release_date);
        expandtxt = findViewById(R.id.expand_text_view);

        //setting trailers recyclerView and adapter
        rvTrailers = findViewById(R.id.trailerList);
        rvTrailers.setLayoutManager(new LinearLayoutManager(this));
        rvTrailers.setHasFixedSize(true);
        trailersAdapter = new TrailersAdapter();
        rvTrailers.setAdapter(trailersAdapter);

        //setting reviews recyclerView and adapter
        rvReviews = findViewById(R.id.rvList);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setHasFixedSize(true);
        mAdapter = new reviewAdapter();
        rvReviews.setAdapter(mAdapter);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Get the object from the intent using the MovieObject key
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("MovieObject")) {
                movieDetail = (MovieClass) getIntent().getParcelableExtra("MovieObject");
                movieId = movieDetail.getId();
                //get Image from movie object and load using picasso
                String imageUrl = movieDetail.getImage();
                Picasso.with(this).load(imageUrl).into(movieImage);

                // get the Textviews populated
                titleText.setText(movieDetail.getTitle());
                rating.setText(movieDetail.getRating());
                releaseDate.setText(movieDetail.getReleaseDate());
                expandtxt.setText(movieDetail.getOverview());
                if(savedInstanceState != null){
                    reviews = savedInstanceState.getParcelableArrayList(MOVIES_REVIEWS);
                    trailers = savedInstanceState.getParcelableArrayList(MOVIES_TRAILERS);
                    setMovieAdapter(reviews,trailers);
                }else {
                    loadReviewsVideos(movieId);
                }
                DealWithFavoriteButton();
            }
        }
    }

    //if the movie has not been favorized, it will show thumb up icon, once the user favorizes
    // the movie, it will be added to database(favorites) and the icon will change to thumb down.
    //If the icon (thumb down) is pressed the movie will be removed from database(favorites)
    public void DealWithFavoriteButton() {
        // Create a new map of values, where column names are the keys
        final ContentValues values = new ContentValues();
        values.put(MoviesProvider.MOVIE_TITLE, movieDetail.getTitle());
        values.put(MoviesProvider.MOVIE_ID, movieDetail.getId());

        final Uri SingleUri = ContentUris.withAppendedId(MoviesProvider
                .CONTENT_URI, movieDetail.getId());
        // Find this entry in the db so we can set the button correctly.
        Cursor cursor = getContentResolver()
                .query(SingleUri, null,null,null, null);

        final Button button = findViewById(R.id.trailer_thump);

        if (cursor.moveToFirst()) {
            button.setBackground(this.getDrawable(baseline_thumb_down_black_18dp));
        } else {
            button.setBackground(this.getDrawable(baseline_thumb_up_black_18dp));
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Cursor cursor = getContentResolver().query(SingleUri, null,
                            null,null, null);
                    if (cursor.moveToFirst()) {
                        getContentResolver().delete(SingleUri, null,null);
                        button.setBackground(getApplicationContext().getDrawable(
                                baseline_thumb_up_black_18dp));
                    } else {
                        getContentResolver().insert(MoviesProvider.CONTENT_URI, values);
                        button.setBackground(getApplicationContext().getDrawable(
                                baseline_thumb_down_black_18dp));
                    }
                    cursor.close();
                } catch (Exception e) {
                    System.out.println("Error:"+e);
                }
            }
        });
        cursor.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                if(!reviews.isEmpty() || !trailers.isEmpty()){
                    reviews.clear();
                    trailers.clear();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIES_REVIEWS, reviews);
        outState.putParcelableArrayList(MOVIES_TRAILERS, trailers);
    }

    @Override
    public void onBackPressed() {
        if(!reviews.isEmpty() || !trailers.isEmpty()){
            reviews.clear();
            trailers.clear();
        }
        super.onBackPressed();
    }

    private void loadReviewsVideos(int id){
        String idMovie = Integer.toString(id);
        String movieReviewUrl = "https://api.themoviedb.org/3/movie/"
                + idMovie + "?append_to_response=videos,reviews&";
        //check network connection
        NetworkCheck check = new NetworkCheck(this, this.findViewById(R.id.main_scroll));
        if (check.isNetworkAvailable() && check.isOnline()){
            URL movieUrl = NetworkUtils.buildUrl(movieReviewUrl);
            new idQueryTask().execute(movieUrl);
        }else {
            check.showErrorMessage();
        }
    }

    private void setMovieAdapter(ArrayList<Review>reviews, ArrayList<Trailers>trailers){
        mAdapter.setReviewData(reviews);
        mAdapter.notifyDataSetChanged();
        trailersAdapter.setTrailerData(trailers);
        trailersAdapter.notifyDataSetChanged();
    }

    //perform background operations
    public class idQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            if(urls == null){
                return null;
            }
            URL searchUrl = urls[0];
            String result = null;
            try {
                result = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            }catch (IOException e){
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result != null && !result.equals("")){
                reviews = JsonUtil.parseReviewFromJson(result);
                trailers = JsonUtil.parseTrailersFromJson(result);
                setMovieAdapter(reviews, trailers);
            }
        }
    }
}


