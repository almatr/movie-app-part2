package com.example.android.myapplication;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.android.myapplication.Model.Review;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;

public class reviewAdapter extends RecyclerView.Adapter<reviewAdapter.ViewHolder> {
    //private Context mContext;
    private ArrayList<Review> mReview;

    //constructor
    public reviewAdapter(){}

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView review_author;
        ExpandableTextView review_cont;

        public ViewHolder(View itemView) {
            super(itemView);
            review_author = itemView.findViewById(R.id.reviewer);
            review_cont = itemView.findViewById(R.id.expand_review_view);
        }
    }

    @NonNull
    @Override
    public reviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View reviewView = inflater.inflate(R.layout.review, parent, false);
        ViewHolder viewHolder = new ViewHolder(reviewView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull reviewAdapter.ViewHolder holder, int position) {
        Review reviewObj = mReview.get(position);
        ExpandableTextView reviewContent = holder.review_cont;
        reviewContent.setText(reviewObj.getReview());
        TextView reviewAuthor = holder.review_author;
        reviewAuthor.setText(reviewObj.getReviewAuthor());
    }

    @Override
    public int getItemCount() {
        if(mReview == null) return 0;
        return mReview.size();
    }

    public void setReviewData(ArrayList<Review> reviews) {
        mReview = reviews;
        notifyDataSetChanged();
    }
}