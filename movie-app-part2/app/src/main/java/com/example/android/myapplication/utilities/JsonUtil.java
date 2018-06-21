package com.example.android.myapplication.utilities;

import android.util.Log;

import com.example.android.myapplication.Model.MovieClass;
import com.example.android.myapplication.Model.Review;
import com.example.android.myapplication.Model.Trailers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class JsonUtil {

    // Define strings to use in movie object creation
    private static final String RESULTS = "results";
    private static final String POSTER_PATH = "poster_path";
    private static final String TITLE = "title";
    private static final String OVERVIEW = "overview";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String RELEASE_DATE = "release_date";
    private static final String ID = "id";
    private static final String REVIEW = "content";
    private static final String REVIEW_AUTHOR = "author";
    private static final String REVIEWS = "reviews";
    private static final String VIDEOS = "videos";
    private static final String TRAILER_KEY = "key";

    // Define string to use for setting image
    private static final String BASE_URL = "http://image.tmdb.org/t/p/w185/";

    //ArrayList of MovieClass to store movie objects
    public static ArrayList<MovieClass> imageArray = new ArrayList<>();

    //ArrayList of Review class to store review objects
    public static ArrayList<Review> reviewArray = new ArrayList<>();

    public static ArrayList<Trailers> trailersArray = new ArrayList<>();

    // Function taking JSON ArrayList of string, setting movie object and adding object into
    // ArrayList of MovieClass
    public static ArrayList<MovieClass> parseMoviefromJson(ArrayList<String> jsonResults){
        try {
            for (String jsonResult: jsonResults) {
                JSONObject responseObj = new JSONObject(jsonResult);
                JSONArray array = new JSONArray();
                if (responseObj.has(RESULTS)) {
                    array = responseObj.getJSONArray(RESULTS);
                } else {
                    array.put(responseObj);
                }
                for (int i = 0; i < array.length(); i++) {
                    MovieClass Movies = new MovieClass();
                    JSONObject imageobject = array.getJSONObject(i);
                    Movies.setImage(BASE_URL + imageobject.optString(POSTER_PATH));
                    Movies.setTitle(imageobject.optString(TITLE));
                    Movies.setOverview(imageobject.optString(OVERVIEW));
                    Movies.setRating(imageobject.optString(VOTE_AVERAGE));
                    Movies.setReleaseDate(imageobject.optString(RELEASE_DATE).substring(0, 4));
                    Movies.setId(imageobject.optInt(ID));
                    imageArray.add(Movies);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return imageArray;
    }

    //parsing reviews from json result
    public static ArrayList<Review> parseReviewFromJson(String reviewJson){
        try{
            JSONObject responseObj = new JSONObject(reviewJson).getJSONObject(REVIEWS);
            JSONArray array = responseObj.getJSONArray(RESULTS);
            for (int i = 0; i < array.length(); i++){
                Review review = new Review();
                JSONObject reviewobject = array.getJSONObject(i);
                review.setReview(reviewobject.optString(REVIEW));
                review.setReviewAuthor(reviewobject.optString(REVIEW_AUTHOR));
                reviewArray.add(review);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return reviewArray;
    }

    //parsing trailers from json result
    public static ArrayList<Trailers> parseTrailersFromJson(String trailerJson){
        try{
            JSONObject responseObj = new JSONObject(trailerJson).getJSONObject(VIDEOS);
            JSONArray array = responseObj.getJSONArray(RESULTS);
            for (int i = 0; i < array.length(); i++){
                Trailers trailers = new Trailers();
                JSONObject trailerobject = array.getJSONObject(i);
                trailers.setTrailerKey(trailerobject.optString(TRAILER_KEY));
                trailersArray.add(trailers);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return trailersArray;
    }
}

