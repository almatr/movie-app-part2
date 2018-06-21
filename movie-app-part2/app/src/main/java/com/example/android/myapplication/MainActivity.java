package com.example.android.myapplication;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.android.myapplication.Data.MoviesProvider;
import com.example.android.myapplication.Model.MovieClass;
import com.example.android.myapplication.utilities.JsonUtil;
import com.example.android.myapplication.utilities.NetworkCheck;
import com.example.android.myapplication.utilities.NetworkUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //for saving menu position key
    private static int mMenuPosition = 0;
    private static final String POSITION_KEY = "menuKey";

    private static final String MOVIES_ARRAY = "moviesArray";
    //for saving recyclerView scroll position
    private static final String SCROLLED_POSITION = "scrolled_position";
    private int mScrolledPosition = 0;

    // Declaring RecyclerView, MovieAdapter and ArrayList of MovieClass objects
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private GridLayoutManager gridManager;
    private ArrayList<MovieClass> mMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerview_movies);
        mRecyclerView.setHasFixedSize(false);

        final int columnsNum = getResources().getInteger(R.integer.gallery_columns);

        gridManager = new GridLayoutManager(
                MainActivity.this,columnsNum);
        mRecyclerView.setLayoutManager(gridManager);
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);
        if (savedInstanceState != null) {
            mMenuPosition = savedInstanceState.getInt(POSITION_KEY);
            mScrolledPosition = savedInstanceState.getInt(SCROLLED_POSITION);
            mMovies = savedInstanceState.getParcelableArrayList(MOVIES_ARRAY);
            mMovieAdapter.setMovieData(mMovies);
        }else{
            loadMovieData(mMenuPosition);
        }
    }

    private void loadMovieData(int movieItemKey){
        String movieUrlStr = getResources().getString(R.string.popular_url);
        mMenuPosition = movieItemKey;
        URL movieUrl;
        ArrayList<URL> moviIdUrl = new ArrayList<URL>();
        switch(movieItemKey) {
            case 0:
                movieUrlStr = getResources().getString(R.string.popular_url);
                moviIdUrl.add(NetworkUtils.buildUrl(movieUrlStr));
                break;
            case 1:
                movieUrlStr = getResources().getString(R.string.topRated_url);
                moviIdUrl.add(NetworkUtils.buildUrl(movieUrlStr));
                break;
            case 2:
                Cursor cursor = getContentResolver().query(MoviesProvider.CONTENT_URI,
                        null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        String id = cursor.getString(0);
                        movieUrlStr = getResources().getString(R.string.movieById) + id + "?";
                        moviIdUrl.add(NetworkUtils.buildUrl(movieUrlStr));
                    } while (cursor.moveToNext());
                }
                cursor.close();
                break;
            default:
                break;
        }
        //check network connection
        NetworkCheck check = new NetworkCheck(this,mRecyclerView);
        if (check.isNetworkAvailable() && check.isOnline()){
            new movieQueryTask().execute(moviIdUrl);
        }else{
            check.showErrorMessage();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        loadMovieData(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.spinner, menu);
        MenuItem item = menu.findItem(R.id.spinner_movies);
        Spinner spinner = (Spinner) item.getActionView();
        //Creating an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.movies_array, android.R.layout.simple_spinner_dropdown_item);
        //Setting adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(mMenuPosition, false);
        spinner.setOnItemSelectedListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION_KEY, mMenuPosition);
        outState.putInt(SCROLLED_POSITION, ((GridLayoutManager)
                mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition());
        outState.putParcelableArrayList(MOVIES_ARRAY, mMovies);
    }

    //perform background operations
    public class movieQueryTask extends AsyncTask<ArrayList<URL>, Void, ArrayList<String>>{
        @Override
        protected ArrayList<String> doInBackground(ArrayList<URL>... urls) {
            if(urls == null){
                return null;
            }
            ArrayList<String> result = new ArrayList<String>();
            ArrayList<URL> urlArray = urls[0];
            for (URL searchUrl: urlArray) {
                try {
                    result.add(NetworkUtils.getResponseFromHttpUrl(searchUrl));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<String> resultArray) {
            if(mMovies != null){
                mMovies.clear();
            }
            if (resultArray != null && !resultArray.isEmpty()) {
                //JsonUtil returns JSON result which is parsed into ArrayList of MovieClass objects
                mMovies = JsonUtil.parseMoviefromJson(resultArray);
            }
            mMovieAdapter.setMovieData(mMovies); //setting movies in mMovieAdapter;
            if (mScrolledPosition != 0) {
                mRecyclerView.scrollToPosition(mScrolledPosition);
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mMovies != null && mMenuPosition == 2) {
            Cursor cursor = getContentResolver().query(MoviesProvider.CONTENT_URI,
                    null, null, null, null);
            if (cursor.getCount() != mMovies.size()) {
                loadMovieData(mMenuPosition);
            }
            cursor.close();
        }
    }
}
