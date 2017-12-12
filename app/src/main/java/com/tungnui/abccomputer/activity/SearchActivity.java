package com.tungnui.abccomputer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tungnui.abccomputer.R;
import com.tungnui.abccomputer.adapter.ProductListAdapter;
import com.tungnui.abccomputer.data.constant.AppConstants;
import com.tungnui.abccomputer.listener.EndlessRecyclerViewScrollListener;
import com.tungnui.abccomputer.listener.ListDialogActionListener;
import com.tungnui.abccomputer.listener.OnItemClickListener;
import com.tungnui.abccomputer.model.Category;
import com.tungnui.abccomputer.model.ProductDetail;
import com.tungnui.abccomputer.network.helper.RequestCategory;
import com.tungnui.abccomputer.network.helper.RequestSearchProducts;
import com.tungnui.abccomputer.network.http.ResponseListener;
import com.tungnui.abccomputer.utils.ActivityUtils;
import com.tungnui.abccomputer.utils.AppUtility;
import com.tungnui.abccomputer.utils.ListTypeShow;
import com.tungnui.abccomputer.utils.RVEmptyObserver;

import java.util.ArrayList;

/**
 * Created by Nasir on 5/15/17.
 */

public class SearchActivity extends BaseActivity {

    // initialize variables
    private Context mContext;
    private Activity mActivity;

    private RecyclerView rvProductList;
    private ArrayList<ProductDetail> productList;
    private ArrayList<Category> categoryList;
    private String[] categoryArray;
    private ProductListAdapter mProductListAdapter;

    protected LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView.LayoutManager mLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private LinearLayoutManager linearLayoutManager;
    private static final int COLUMN_SPAN_COUNT = 2;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    // initialize View
    private Toolbar mToolbar;
    private SearchView mSearchView;
    private ImageView ivListTypeIcon;
    private ToggleButton btnToggleExpand;
    private RelativeLayout lytSearchAttribute;
    private LinearLayout lytSearchHeader, lytSortingData, noDataView, categoryChooser;
    private EditText edtMinPrice, edtMaxPrice;
    private TextView tvSortingName, selectedCat;
    private ProgressBar loadMoreView;

    // listener
    private EndlessRecyclerViewScrollListener scrollListener;

    // initialize search key
    private int pageNumber = AppConstants.INITIAL_PAGE_NUMBER;
    private String searchKey = AppConstants.EMPTY_STRING;
    private String order = AppConstants.KEY_ASC;
    private String orderBy = AppConstants.KEY_TITLE;
    private String categoryId = AppConstants.EMPTY_STRING;

    private boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();
        initSearchToolbar();
        loadData();
        loadCategories();
        initListener();
    }

    private void initVariable() {
        mContext = getApplicationContext();
        mActivity = SearchActivity.this;

        productList = new ArrayList<>();
        categoryList = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.activity_search);

        // from base class
        initLoader();

        rvProductList = (RecyclerView) findViewById(R.id.rvProductList);
        ivListTypeIcon = (ImageView) findViewById(R.id.ivListTypeIcon);
        btnToggleExpand = (ToggleButton) findViewById(R.id.btnToggleExpand);
        lytSearchAttribute = (RelativeLayout) findViewById(R.id.lytSearchAttribute);
        lytSearchHeader = (LinearLayout) findViewById(R.id.lytSearchHeader);
        lytSortingData = (LinearLayout) findViewById(R.id.lytSortingData);
        categoryChooser = (LinearLayout) findViewById(R.id.categoryChooser);
        noDataView = (LinearLayout) findViewById(R.id.noDataView);
        mSearchView = (SearchView) findViewById(R.id.searchView);
        mSearchView.setIconified(false);

        edtMinPrice = (EditText) findViewById(R.id.edtMinPrice);
        edtMaxPrice = (EditText) findViewById(R.id.edtMaxPrice);
        tvSortingName = (TextView) findViewById(R.id.tvSortingName);
        selectedCat = (TextView) findViewById(R.id.selectedCat);
        loadMoreView = (ProgressBar) findViewById(R.id.loadMore);

        // init RecyclerView
        rvProductList.setHasFixedSize(true);
        setRecyclerViewLayoutManager(rvProductList, LayoutManagerType.LINEAR_LAYOUT_MANAGER);
        mProductListAdapter =  new ProductListAdapter(mContext,productList,ListTypeShow.LINEAR);
        mProductListAdapter.registerAdapterDataObserver(new RVEmptyObserver(rvProductList, noDataView));
        rvProductList.setAdapter(mProductListAdapter);

    }

    private void initSearchToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void loadData() {

        Intent intent = this.getIntent();
        searchKey = intent.getStringExtra(AppConstants.SEARCH_KEY);

        mSearchView.setQuery(searchKey, false);

        loadSearchProducts(false, false);
    }

    private void initListener() {
       mProductListAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemListener(View view, int position) {
                // invoke product details activity by product id
                ActivityUtils.Companion.getInstance().invokeProductDetails(mActivity, productList.get(position).id);
            }
        });

        lytSortingData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppUtility.showSortingAttributesDialog(mActivity, getResources().getString(R.string.select_order), AppConstants.orderTitles, new ListDialogActionListener() {
                    @Override
                    public void onItemSelected(int position) {
                        tvSortingName.setText(AppConstants.orderTitles[position]);
                        order = AppConstants.KEY_ASC;
                        orderBy = AppConstants.orderValues[position];

                        loadSearchProducts(false, false);
                    }
                });
            }
        });


        categoryChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppUtility.showSortingAttributesDialog(mActivity, getResources().getString(R.string.select_category), categoryArray, new ListDialogActionListener() {
                    @Override
                    public void onItemSelected(int position) {

                        if (!categoryList.isEmpty()) {
                            int id = categoryList.get(position).id;
                            if (id > 0) {
                                categoryId = String.valueOf(id);
                                loadSearchProducts(false, true);
                            } else {
                                loadSearchProducts(false, false);
                            }
                            selectedCat.setText(AppUtility.showHtml(categoryList.get(position).name));

                        }
                    }
                });
            }
        });

        ivListTypeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentLayoutManagerType == LayoutManagerType.LINEAR_LAYOUT_MANAGER) {
                    ivListTypeIcon.setImageResource(R.drawable.ic_list);
                    setRecyclerViewLayoutManager(rvProductList, LayoutManagerType.GRID_LAYOUT_MANAGER);

                    mProductListAdapter = new ProductListAdapter(mContext, productList, ListTypeShow.GRID);
                    rvProductList.setAdapter(mProductListAdapter);

                } else {
                    ivListTypeIcon.setImageResource(R.drawable.ic_grid);
                    setRecyclerViewLayoutManager(rvProductList, LayoutManagerType.LINEAR_LAYOUT_MANAGER);

                    mProductListAdapter = new ProductListAdapter(mContext, productList, ListTypeShow.LINEAR);
                    rvProductList.setAdapter(mProductListAdapter);
                }
            }
        });

        btnToggleExpand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lytSearchAttribute.setVisibility(View.VISIBLE);
                    // rotate
                    btnToggleExpand.animate().rotation(180).start();
                } else {
                    lytSearchAttribute.setVisibility(View.GONE);
                    // return rotate
                    btnToggleExpand.animate().rotation(0).start();
                }
            }
        });

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                loadSearchProducts(false, false);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String key) {
                return false;
            }
        });

        edtMaxPrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (!productList.isEmpty()) {
                    productList.clear();
                }
                loadSearchProducts(false, false);
                return false;
            }
        });

        rvProductList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    onScrolledMore();
                }
            }
        });


    }

    // Generate list and grid layout manager
    public void setRecyclerViewLayoutManager(RecyclerView mRecyclerView, LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                gridLayoutManager = new GridLayoutManager(mActivity, COLUMN_SPAN_COUNT);
                mLayoutManager = gridLayoutManager;
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                linearLayoutManager = new LinearLayoutManager(mActivity);
                mLayoutManager = linearLayoutManager;
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

                break;
            default:
                linearLayoutManager = new LinearLayoutManager(mActivity);
                mLayoutManager = linearLayoutManager;
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
        //lazyLoaderListener(mLayoutManager);
    }


    private void loadCategories() {
        // Load category list
        RequestCategory requestCategory = new RequestCategory(this);
        requestCategory.setResponseListener(new ResponseListener() {
            @Override
            public void onResponse(Object data) {
                if (data != null) {

                    ArrayList<Category> arrayList = (ArrayList<Category>) data;
                    categoryList.clear();
                    categoryList.add(new Category(-1, getString(R.string.all), null, null, false)); // id -1 means all
                    categoryList.addAll(arrayList);

                    categoryArray = null;
                    categoryArray = new String[categoryList.size()];

                    for (int i = 0; i < categoryList.size(); i++) {
                        categoryArray[i] = categoryList.get(i).name;
                    }

                }
            }
        });
        requestCategory.execute();
    }

    private void loadSearchProducts(final boolean loadMore, boolean withCategory) {

        if (!loadMore) {
            showLoader();
        } else {
            loadMoreView.setVisibility(View.VISIBLE);
        }

        //set value
        searchKey = mSearchView.getQuery().toString();
        String minPrice = edtMinPrice.getText().toString();
        String maxPrice = edtMaxPrice.getText().toString();

        RequestSearchProducts searchProduct;
        if (withCategory) {
            searchProduct = new RequestSearchProducts(mActivity, pageNumber, searchKey, categoryId, minPrice, maxPrice, order, orderBy);
        } else {
            searchProduct = new RequestSearchProducts(mActivity, pageNumber, searchKey, minPrice, maxPrice, order, orderBy);
        }
        searchProduct.setResponseListener(new ResponseListener() {
            @Override
            public void onResponse(Object data) {

                if (data != null) {
                    // visible filter view
                    lytSearchHeader.setVisibility(View.VISIBLE);

                    if (!loadMore) {
                        productList.clear();
                    }

                    productList.addAll((ArrayList<ProductDetail>) data);
                    mProductListAdapter.notifyDataSetChanged();
                }

                loadMoreView.setVisibility(View.GONE);

                hideLoader();
                loading = true;

            }
        });
        searchProduct.execute();
    }

    private void onScrolledMore() {
        int visibleItemCount, totalItemCount, pastVisiblesItems;
        if (mCurrentLayoutManagerType == LayoutManagerType.GRID_LAYOUT_MANAGER) {
            visibleItemCount = gridLayoutManager.getChildCount();
            totalItemCount = gridLayoutManager.getItemCount();
            pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();
        } else {
            visibleItemCount = linearLayoutManager.getChildCount();
            totalItemCount = linearLayoutManager.getItemCount();
            pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
        }
        if (loading) {
            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                loading = false;
                loadMoreView.setVisibility(View.VISIBLE);
                pageNumber = pageNumber + 1;
                if (categoryId.isEmpty()) {
                    loadSearchProducts(true, false);
                } else {
                    loadSearchProducts(true, true);
                }
            }
        }
    }

}
