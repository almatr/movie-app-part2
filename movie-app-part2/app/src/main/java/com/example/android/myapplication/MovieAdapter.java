package com.example.android.myapplication;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.myapplication.Model.MovieClass;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{

    private Context mContext;
    public ArrayList<MovieClass> MovieData;

    //constructor for MovieAdapter
    public MovieAdapter(Context context){
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView movieImage;

        public ViewHolder(View view) {
            super(view);
            movieImage = (ImageView) view.findViewById(R.id.movie_img);
        }
    }

    @NonNull
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_image;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup,
                shouldAttachToParentImmediately);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieAdapter.ViewHolder holder, final int position) {
        String url = MovieData.get(position).getImage();
        Picasso.with(mContext).load(url).into(holder.movieImage);
        holder.movieImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mContext, MovieDetail.class); //create intent
                mIntent.putExtra("MovieObject", MovieData.get(position)); //pass object & key
                if(mIntent.resolveActivity(mContext.getPackageManager())!= null){
                    mContext.startActivity(mIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(MovieData == null) return 0;
        return MovieData.size();
    }

    //Setting MovieData
    public void setMovieData(ArrayList<MovieClass> movies) {
        MovieData = movies;
        notifyDataSetChanged();
    }
}
