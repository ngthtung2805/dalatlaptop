package com.tungnui.dalatlaptop.interfaces;

import android.view.View;

import com.tungnui.dalatlaptop.entities.product.Product;

public interface RelatedProductsRecyclerInterface {

    void onRelatedProductSelected(View v, int position, Product product);
}
