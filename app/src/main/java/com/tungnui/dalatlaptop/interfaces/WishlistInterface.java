package com.tungnui.dalatlaptop.interfaces;

import android.view.View;

import com.tungnui.dalatlaptop.entities.wishlist.WishlistItem;

public interface WishlistInterface {

    void onWishlistItemSelected(View view, WishlistItem wishlistItem);

    void onRemoveItemFromWishList(View caller, WishlistItem wishlistItem, int adapterPosition);
}
