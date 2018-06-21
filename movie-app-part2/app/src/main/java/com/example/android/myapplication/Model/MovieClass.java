package com.example.android.myapplication.Model;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MovieClass implements Parcelable {

    //define Strings for MovieClass
    private String image;
    private String title;
    private String overview;
    private String rating;
    private String releaseDate;
    private int id;

    //constructor for the MovieClass
    public MovieClass(){ }

    //Setters
    public void setImage(String image){
        this.image = image;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setOverview(String overview){
        this.overview = overview;
    }
    public void setRating(String rating){
        this.rating = rating;
    }
    public void setReleaseDate(String releaseDate){
        this.releaseDate = releaseDate;
    }
    public void setId(int id){
        this.id = id;
    }

    //Getters
    public String getImage() {
        return image;
    }
    public String getTitle() {
        return title;
    }
    public String getOverview() {
        return overview;
    }
    public String getRating() {
        return rating;
    }
    public String getReleaseDate(){
        return releaseDate;
    }
    public int getId(){
        return id;
    }

    //Required methods to implement Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    //writes the data to the Parcel for data delivery
    //same order of read must be implemented
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.image);
        dest.writeString(this.title);
        dest.writeString(this.overview);
        dest.writeString(this.rating);
        dest.writeString(this.releaseDate);
        dest.writeInt(this.id);
    }

    //retrieves values we originaly wrote into the Parcel. Will be accessed by "CREATOR" field
    //using same order as in writeToParcel
    private MovieClass(Parcel in){
        this.image = in.readString();
        this.title = in.readString();
        this.overview = in.readString();
        this.rating = in.readString();
        this.releaseDate = in.readString();
        this.id = in.readInt();
    }

    //Parcelable.Creator<MovieClass> CREATOR` constant for the class;
    public static final Parcelable.Creator<MovieClass> CREATOR
            = new Parcelable.Creator<MovieClass>(){
        // calls the new constructor and passes along the unmarshalled `Parcel`
        // it returns the new object
        @Override
        public MovieClass createFromParcel(Parcel source) {
            return new MovieClass(source);
        }
        @Override
        public MovieClass[] newArray(int size) {
            return new MovieClass[size];
        }
    };
}
