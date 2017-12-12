package com.tungnui.abccomputer.adapter;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tungnui.abccomputer.R;
import com.tungnui.abccomputer.data.constant.AppConstants;
import com.tungnui.abccomputer.listener.OnItemClickListener;
import com.tungnui.abccomputer.model.AttributeValueModel;
import com.tungnui.abccomputer.model.ProductAttribute;

import java.util.ArrayList;

public class AttributeAdapter extends RecyclerView.Adapter<AttributeAdapter.ViewHolder> {

    private static Context mContext;
    private ArrayList<ProductAttribute> dataList;

    public AttributeAdapter(Context context, ArrayList<ProductAttribute> dataList) {
        this.mContext = context;
        this.dataList = dataList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAttributeName;
        private AttributeValueAdapter horizontalListAdapter;
        private RecyclerView rvAttributeValue;
        private ArrayList<AttributeValueModel> arrayList;

        public ViewHolder(final View itemView, int viewType) {
            super(itemView);
            Context context = itemView.getContext();
            tvAttributeName = (TextView) itemView.findViewById(R.id.tvAttributeName);

            rvAttributeValue = (RecyclerView) itemView.findViewById(R.id.rvAttributeValue);
            rvAttributeValue.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

            arrayList = new ArrayList<>();
            horizontalListAdapter = new AttributeValueAdapter(mContext, arrayList);
            rvAttributeValue.setAdapter(horizontalListAdapter);

            horizontalListAdapter.setItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemListener(View view, int position) {

                    for(AttributeValueModel attributeValueModel : arrayList) {
                        attributeValueModel.setSelect(false);
                    }
                    arrayList.get(position).setSelect(true);
                    horizontalListAdapter.notifyDataSetChanged();
                 }
            });

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_atribute, parent, false);
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        // set attribute name
        holder.tvAttributeName.setText(dataList.get(position).name);

        // set attribute value
        if (!holder.arrayList.isEmpty()) {
            holder.arrayList.clear();
        }
        holder.arrayList.addAll(dataList.get(position).optionList);
        holder.horizontalListAdapter.notifyDataSetChanged(); // List of Strings

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public String getSelectedAttributes() {
        String selectedAttributes = "";
        for(ProductAttribute productAttribute : dataList) {
            for (AttributeValueModel attributeValueModel : productAttribute.optionList) {
                if(attributeValueModel.isSelect()) {
                    if (selectedAttributes.isEmpty()) {
                        selectedAttributes += attributeValueModel.getName();
                    } else {
                        selectedAttributes += AppConstants.COMMA + attributeValueModel.getName();
                    }
                }
            }
        }
        return selectedAttributes;
    }

}
