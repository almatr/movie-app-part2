package com.example.android.myapplication.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {

    private String review;
    private String reviewAuthor;

    public Review(){}

    //setters
    public void setReview(String review){
        this.review = review;
    }
    public void setReviewAuthor(String reviewAuthor){
        this.reviewAuthor = reviewAuthor;
    }

    //getters
    public String getReview() {
        return review;
    }
    public String getReviewAuthor() {
        return reviewAuthor;
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
        dest.writeString(this.review);
        dest.writeString(this.reviewAuthor);
    }

    //retrieves values we originaly wrote into the Parcel. Will be accessed by "CREATOR" field
    //using same order as in writeToParcel
    private Review(Parcel in){
        this.review = in.readString();
        this.reviewAuthor = in.readString();
    }

    //Parcelable.Creator<MovieClass> CREATOR` constant for the class;
    public static final Parcelable.Creator<Review> CREATOR
            = new Parcelable.Creator<Review>(){
        // calls the new constructor and passes along the unmarshalled `Parcel`
        // it returns the new object
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }
        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
