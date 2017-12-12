package com.tungnui.abccomputer.adapter;


import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tungnui.abccomputer.R;
import com.tungnui.abccomputer.data.constant.AppConstants;
import com.tungnui.abccomputer.listener.OnItemClickListener;
import com.tungnui.abccomputer.model.Product;
import com.tungnui.abccomputer.model.ProductDetail;
import com.tungnui.abccomputer.utils.ListTypeShow;

import java.util.ArrayList;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ProductDetail> dataList;
    private ListTypeShow listTypeShow;

    // Listener
    public static OnItemClickListener mListener;

    public ProductListAdapter(Context context, ArrayList<ProductDetail> dataList, ListTypeShow listTypeShow) {
        this.mContext = context;
        this.dataList = dataList;
        this.listTypeShow = listTypeShow;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductImage;
        private TextView tvProductName, tvProductPrice, tvRattingCount;
        private RatingBar ratingBar;

        public ViewHolder(final View itemView, int viewType) {
            super(itemView);

            ivProductImage = (ImageView) itemView.findViewById(R.id.ivProductImage);
            tvProductName = (TextView) itemView.findViewById(R.id.tvProductName);
            tvProductPrice = (TextView) itemView.findViewById(R.id.tvProductPrice);
            tvRattingCount = (TextView) itemView.findViewById(R.id.tvRattingCount);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);

            // listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemListener(view, getLayoutPosition());
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (listTypeShow == ListTypeShow.GRID) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_grid, parent, false);
            return new ViewHolder(view, viewType);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_linear, parent, false);
            return new ViewHolder(view, viewType);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final ProductDetail product = dataList.get(position);

        holder.tvProductName.setText(product.name);
        holder.tvRattingCount.setText(String.valueOf(product.averageRating));
        holder.ratingBar.setRating(product.averageRating);

        if (product.onSaleStatus) {
            holder.tvProductPrice.setText(product.sellPrice+"đ");
        } else {
            holder.tvProductPrice.setText(product.regularPrice+"đ");
        }

        if (!product.imageList.isEmpty()) {
            Glide.with(mContext)
                    .load(product.imageList.get(0))
                    .placeholder(R.color.imgPlaceholder)
                    .centerCrop()
                    .into(holder.ivProductImage);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public void setItemClickListener(OnItemClickListener mListener) {
        if (mListener != null) {
            this.mListener = mListener;
        }
    }

    public void setFilter(ArrayList<ProductDetail> productList) {
        dataList = new ArrayList<>();
        dataList.addAll(productList);
        notifyDataSetChanged();
    }
}
