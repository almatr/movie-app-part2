package com.example.android.myapplication.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailers implements Parcelable{
    private String trailerKey;

    public Trailers(){}

    //setter
    public void setTrailerKey(String trailerKey){
        this.trailerKey = trailerKey;
    }

    //getter
    public String getTrailerKey() {
        return trailerKey;
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
        dest.writeString(this.trailerKey);
    }

    //retrieves values we originaly wrote into the Parcel. Will be accessed by "CREATOR" field
    //using same order as in writeToParcel
    private Trailers (Parcel in){
        this.trailerKey = in.readString();
    }

    //Parcelable.Creator<MovieClass> CREATOR` constant for the class;
    public static final Parcelable.Creator<Trailers> CREATOR
            = new Parcelable.Creator<Trailers>(){
        // calls the new constructor and passes along the unmarshalled `Parcel`
        // it returns the new object
        @Override
        public Trailers createFromParcel(Parcel source) {
            return new Trailers(source);
        }
        @Override
        public Trailers[] newArray(int size) {
            return new Trailers[size];
        }
    };
}
