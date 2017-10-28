package com.tungnui.dalatlaptop.interfaces;

import android.view.View;

import com.tungnui.dalatlaptop.entities.drawerMenu.DrawerItemCategory;
import com.tungnui.dalatlaptop.entities.drawerMenu.DrawerItemPage;

public interface DrawerRecyclerInterface {
    void onCategorySelected(View v, DrawerItemCategory drawerItemCategory);
    void onPageSelected(View v, DrawerItemPage drawerItemPage);
    void onHeaderSelected();
}
