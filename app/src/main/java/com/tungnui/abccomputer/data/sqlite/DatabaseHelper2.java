package com.tungnui.abccomputer.data.sqlite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper2 extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = DatabaseHelper.class.getName();
    // Database Version
    public static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "eshopperDB";

    // Table Names
    public static final String TABLE_WISH = "table_wish";
    public static final String TABLE_CART = "table_cart";
    public static final String TABLE_NOTIFICATION = "table_notification";

    // Common column names
    public static final String KEY_ID              = "id";
    public static final String KEY_PRODUCT_ID      = "product_id";
    public static final String KEY_NAME            = "name";
    public static final String KEY_IMAGES          = "images";
    public static final String KEY_PRICE           = "price";
    public static final String KEY_IS_SELECTED     = "is_selected";
    public static final String KEY_QUANTITY        = "quantity";

    // WISH Table - column name
    public static final String KEY_RATTING         = "ratting";
    public static final String KEY_ORDER_COUNT     = "order_count";

    // CART Table - column name

    public static final String KEY_ATTRIBUTE       = "attribute";

    // Notification Table - column name
    public static final String KEY_TYPE     = "type";
    public static final String KEY_TITLE    = "title";
    public static final String KEY_MESSAGE  = "message";
    public static final String KEY_URL      = "url";
    public static final String KEY_IS_READ  = "is_read";

    // WISH table create statement
    private static final String CREATE_TABLE_WISH = "CREATE TABLE "+ TABLE_WISH + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_PRODUCT_ID + " INTEGER,"
            + KEY_NAME + " TEXT,"
            + KEY_IMAGES + " TEXT,"
            + KEY_PRICE + " TEXT,"
            + KEY_RATTING + " REAL,"
            + KEY_ORDER_COUNT + " INTEGER)";

    // CART table create statement
    private static final String CREATE_TABLE_CART = "CREATE TABLE "+ TABLE_CART + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_PRODUCT_ID + " INTEGER,"
            + KEY_NAME + " TEXT,"
            + KEY_IMAGES + " TEXT,"
            + KEY_PRICE + " REAL,"
            + KEY_QUANTITY + " INTEGER,"
            + KEY_ATTRIBUTE + " TEXT,"
            + KEY_IS_SELECTED + " INTEGER)";

    // Notification table create statement
    private static final String CREATE_TABLE_NOTIFICATION = "CREATE TABLE "+ TABLE_NOTIFICATION + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_TYPE + " TEXT,"
            + KEY_TITLE + " TEXT,"
            + KEY_MESSAGE + " TEXT,"
            + KEY_URL + " TEXT,"
            + KEY_PRODUCT_ID + " INTEGER,"
            + KEY_IS_READ + " TEXT)";

    private static DatabaseHelper dbHelper = null;

    public static DatabaseHelper getInstance(Context context) {
        if(dbHelper == null) {
            dbHelper =  new DatabaseHelper(context);
        }
        return dbHelper;
    }

    public DatabaseHelper2(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_WISH);
        db.execSQL(CREATE_TABLE_CART);
        db.execSQL(CREATE_TABLE_NOTIFICATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WISH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
        // create new tables
        onCreate(db);
    }
}
