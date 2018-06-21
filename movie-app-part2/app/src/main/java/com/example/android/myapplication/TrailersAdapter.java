package com.example.android.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.android.myapplication.Model.Trailers;

import java.util.ArrayList;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {
    //private Context mContext;
    private ArrayList<Trailers> mTrailers;

    //constructor
    public TrailersAdapter(){}

    public class ViewHolder extends RecyclerView.ViewHolder{

        Button trailer_button;
        TextView trailer_text;

        public ViewHolder(View itemView) {
            super(itemView);
            trailer_button = itemView.findViewById(R.id.trailer_button);
            trailer_text = itemView.findViewById(R.id.play_trailer);
        }
    }

    @NonNull
    @Override
    public TrailersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View trailerView = inflater.inflate(R.layout.trailer_videos, parent, false);
        ViewHolder viewHolder = new ViewHolder(trailerView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrailersAdapter.ViewHolder holder, int position) {
        Trailers trailerObj = mTrailers.get(position);
        int trailerSequence = position + 1;
        Button button = holder.trailer_button;
        holder.trailer_text.setText("Play Trailer " + Integer.toString(trailerSequence));
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                "http://www.youtube.com/watch?v=" + trailerObj.getTrailerKey()));
        intent.setPackage("com.google.android.youtube");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                  view.getContext().startActivity(intent);
                } catch (Exception e) {
                    System.out.println("Error:"+e);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mTrailers == null) return 0;
        return mTrailers.size();
    }

    public void setTrailerData(ArrayList<Trailers> trailers) {
        mTrailers = trailers;
        notifyDataSetChanged();
    }
}