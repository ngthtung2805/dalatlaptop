package com.tungnui.abccomputer.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.tungnui.abccomputer.R;
import com.tungnui.abccomputer.model.ProductReview;

import java.util.ArrayList;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {

    private ArrayList<ProductReview> dataList;

    public ReviewListAdapter(ArrayList<ProductReview> dataList) {
        this.dataList = dataList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCustomerName, tvReview, tvRatingValue;
        private RatingBar ratingBar;

        public ViewHolder(final View itemView, int viewType) {
            super(itemView);

            tvCustomerName = (TextView) itemView.findViewById(R.id.tvCustomerName);
            tvReview = (TextView) itemView.findViewById(R.id.tvReview);
            tvRatingValue = (TextView) itemView.findViewById(R.id.tvRatingValue);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
            return new ViewHolder(view,viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final ProductReview review = dataList.get(position);

        holder.tvCustomerName.setText(review.name);
        holder.tvReview.setText(review.review);
        holder.ratingBar.setRating(review.rating);
        holder.tvRatingValue.setText(String.valueOf(review.rating));

    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
