package com.tungnui.abccomputer.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.tungnui.abccomputer.R;
import com.tungnui.abccomputer.listener.OnItemClickListener;

public class ImageSliderAdapter extends PagerAdapter {
 
    private ArrayList<String> images;
    private LayoutInflater inflater;
    private Context mContext;

    // Listener
    private OnItemClickListener mListener;
 
    public ImageSliderAdapter(Context context, ArrayList<String> images) {
        this.mContext = context;
        this.images=images;
        inflater = LayoutInflater.from(context);
    }
 
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
 
    @Override
    public int getCount() {
        return images.size();
    }
 
    @Override
    public Object instantiateItem(final ViewGroup view, final int position) {

        View imageLayout = inflater.inflate(R.layout.item_image_slider, view, false);
        final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);


        Glide.with(mContext)
                .load(images.get(position))
                .placeholder(R.color.imgPlaceholder)
                .into(imageView);

        view.addView(imageLayout);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener!=null) {
                    mListener.onItemListener(view, position);
                }
            }
        });

        return imageLayout;
    }
 
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    public void setItemClickListener(OnItemClickListener mListener){
        this.mListener = mListener;
    }

}