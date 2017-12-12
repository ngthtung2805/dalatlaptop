package com.tungnui.abccomputer.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.tungnui.abccomputer.data.constant.AppConstants;
import com.tungnui.abccomputer.model.WishItem;
import java.util.ArrayList;

public class WishDBController {

    private DatabaseHelper2 dbHelper;
    private Context mContext;
    private SQLiteDatabase database;

    public WishDBController(Context context) {
        mContext = context;
    }

    public WishDBController open() throws SQLException {
        dbHelper = new DatabaseHelper2(mContext);
        database = dbHelper.getWritableDatabase();
        return this;
    }
    public void close() {
        dbHelper.close();
    }

    public long insertWishItem(int productId, String name, String images, float price, float ratting, int orderCount) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper2.KEY_PRODUCT_ID, productId);
        contentValue.put(DatabaseHelper2.KEY_NAME, name);
        contentValue.put(DatabaseHelper2.KEY_IMAGES, images);
        contentValue.put(DatabaseHelper2.KEY_PRICE, price);
        contentValue.put(DatabaseHelper2.KEY_RATTING, ratting);
        contentValue.put(DatabaseHelper2.KEY_ORDER_COUNT, orderCount);

        return database.insert(DatabaseHelper2.TABLE_WISH, null, contentValue);
    }

    public ArrayList<WishItem> getAllWishData()
    {
        ArrayList<WishItem> wishList = new ArrayList<>();

        Cursor cursor = database.rawQuery("select * from "+DatabaseHelper2.TABLE_WISH, null);
        if (cursor != null && cursor.getCount() > 0) {
            try {
                cursor.moveToFirst();
                while (cursor.isAfterLast() == false) {

                    int productId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper2.KEY_PRODUCT_ID));
                    String name   = cursor.getString(cursor.getColumnIndex(DatabaseHelper2.KEY_NAME));
                    String images = cursor.getString(cursor.getColumnIndex(DatabaseHelper2.KEY_IMAGES));
                    float price   = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper2.KEY_PRICE));
                    float ratting = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper2.KEY_RATTING));
                    int orderCount= cursor.getInt(cursor.getColumnIndex(DatabaseHelper2.KEY_ORDER_COUNT));

                    if (productId > AppConstants.VALUE_ZERO) {
                        wishList.add(new WishItem(productId, name, images, price, ratting, orderCount));
                    }
                    cursor.moveToNext();
                }
            } catch (Exception ex) {
            }
        }
        return wishList;
    }

    public int updateWishItem(long id, int productId, String name, String images, float price, float ratting, int orderCount) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper2.KEY_PRODUCT_ID, productId);
        contentValues.put(DatabaseHelper2.KEY_NAME, name);
        contentValues.put(DatabaseHelper2.KEY_IMAGES, images);
        contentValues.put(DatabaseHelper2.KEY_PRICE, price);
        contentValues.put(DatabaseHelper2.KEY_RATTING, ratting);
        contentValues.put(DatabaseHelper2.KEY_ORDER_COUNT, orderCount);

        int updateStatus = database.update(DatabaseHelper2.TABLE_WISH, contentValues,
                DatabaseHelper2.KEY_ID + " = " + id, null);
        return updateStatus;
    }

    public void deleteWishItemById(long productId) {
        database.delete(DatabaseHelper2.TABLE_WISH, DatabaseHelper2.KEY_PRODUCT_ID + "=" + productId, null);
    }
    public boolean isAlreadyWished(int productId) {
        Cursor cursor = database.rawQuery("select "+DatabaseHelper2.KEY_PRODUCT_ID+" from " + DatabaseHelper2.TABLE_WISH + " where " + DatabaseHelper2.KEY_PRODUCT_ID + "=" + productId + "", null);
        if(cursor!=null && cursor.getCount()>0){
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }
    public void deleteAllWishData() {
        database.delete(DatabaseHelper2.TABLE_WISH, null, null);
    }

    public int countWishProduct(){
        int numOfRows = (int) DatabaseUtils.queryNumEntries(database, DatabaseHelper2.TABLE_WISH);
        return numOfRows;
    }

    private void dropWishTable() {
        String sql = "drop table " + DatabaseHelper2.TABLE_WISH;
        try {
            database.execSQL(sql);
        } catch (SQLException e) {

        }
    }
//    public class CustomComparator implements Comparator<ProductResponseModel> {
//        @Override
//        public int compare(ProductResponseModel p1, ProductResponseModel p2) {
//            Long t1 = p1.postTimeStamp;
//            Long t2 = p2.postTimeStamp;
//            return t1.compareTo(t2);
//        }
//    }
}
