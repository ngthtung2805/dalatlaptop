package com.tungnui.abccomputer.network.params;

/**
 * Created by Ashiq on 8/13/2016.
 */
public class HttpParams {
  public static final String BASE_URL = "https://abccomputer.tungnui.com/wp-json/wc/v2/";

  public static final String CONSUMER_KEY = "ck_9c06a645be257c36f5b8545064559caf7d7eab05";
  public static final String CONSUMER_SECRET = "cs_eba612121c821ba1fb8c3ba50692a27c204a0226";

  // END POINTS
  public static final String API_CUSTOMER = BASE_URL + "customers";
  public static final String API_SEARCH_CUSTOMER = BASE_URL + "customers?email=";
  public static final String API_EDIT_CUSTOMER = BASE_URL + "customers/";
  public static final String API_CATEGORIES = BASE_URL + "products/categories";
  public static final String API_PRODUCTS = BASE_URL + "products";
  public static final String API_PRODUCTS_LIST = BASE_URL + "products?page=";
  //public static final String API_SEARCH_PRODUCTS = BASE_URL+"products?";
  public static final String API_PRODUCTS_BY_CATEGORY = BASE_URL + "products?category=";
  public static final String API_PRODUCT_DETAILS = BASE_URL + "products/";
  public static final String API_COUPONS = BASE_URL + "coupons";
  public static final String API_PRODUCT_REVIEWS = "/reviews";
  public static final String API_REQUEST_ORDER = BASE_URL + "orders";
  public static final String API_DELETE_ORDER = BASE_URL + "orders/";
  public static final String API_ORDER_DETAILS = BASE_URL + "orders/";
  public static final String API_ORDER_NOTES = "/notes/";
  public static final String API_ORDER_LIST = "orders?customer=";
  // report - top sellers
  public static final String API_REPORT_TOP_SELLERS = BASE_URL + "reports/top_sellers";

  public static final String API_GET_All_COUNTRIES = BASE_URL + "shipping/zones/";
  public static final String API_GET_RELATED_STATES1 = "/locations";


  // product filtered/search key constants
  public static final String KEY_PER_PAGE = "&per_page=";
  public static final String KEY_SEARCH = "&search=";
  public static final String KEY_FEATURED = "&featured=true";
  public static final String KEY_RECENT = "&after=";
  public static final String KEY_CATEGORY = "&category=";
  public static final String KEY_MIN_PRICE = "&min_price=";
  public static final String KEY_MAX_PRICE = "&max_price=";
  public static final String KEY_ORDER = "&order=";
  public static final String KEY_ORDER_BY = "&orderby=";

}
