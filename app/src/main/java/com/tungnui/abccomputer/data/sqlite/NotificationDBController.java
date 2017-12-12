package com.tungnui.abccomputer.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.tungnui.abccomputer.model.NotificationModel;

import java.util.ArrayList;

public class NotificationDBController {

    private DatabaseHelper2 dbHelper;
    private Context mContext;
    private SQLiteDatabase database;

    private static final String READ = "read", UNREAD = "unread";

    public NotificationDBController(Context context) {
        mContext = context;
    }

    public NotificationDBController open() throws SQLException {
        dbHelper = new DatabaseHelper2(mContext);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long insertNotificationItem(String type, String title, String message, int productId, String url) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper2.KEY_TYPE, type);
        contentValue.put(DatabaseHelper2.KEY_TITLE, title);
        contentValue.put(DatabaseHelper2.KEY_MESSAGE, message);
        contentValue.put(DatabaseHelper2.KEY_PRODUCT_ID, productId);
        contentValue.put(DatabaseHelper2.KEY_URL, url);
        contentValue.put(DatabaseHelper2.KEY_IS_READ, UNREAD);

        return database.insert(DatabaseHelper2.TABLE_NOTIFICATION, null, contentValue);
    }

    public ArrayList<NotificationModel> getAllNotification() {

        String[] projection = {
                DatabaseHelper2.KEY_ID,
                DatabaseHelper2.KEY_TYPE,
                DatabaseHelper2.KEY_TITLE,
                DatabaseHelper2.KEY_MESSAGE,
                DatabaseHelper2.KEY_PRODUCT_ID,
                DatabaseHelper2.KEY_URL,
                DatabaseHelper2.KEY_IS_READ
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = DatabaseHelper2.KEY_ID + " DESC";

        Cursor cursor = database.query(
                DatabaseHelper2.TABLE_NOTIFICATION,  // The table name to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        return fetchData(cursor);
    }

    public ArrayList<NotificationModel> getUnreadNotification() {

        String[] projection = {
                DatabaseHelper2.KEY_ID,
                DatabaseHelper2.KEY_TYPE,
                DatabaseHelper2.KEY_TITLE,
                DatabaseHelper2.KEY_MESSAGE,
                DatabaseHelper2.KEY_PRODUCT_ID,
                DatabaseHelper2.KEY_URL,
                DatabaseHelper2.KEY_IS_READ
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = DatabaseHelper2.KEY_ID + " DESC";
        String selection = DatabaseHelper2.KEY_IS_READ + "=?";
        String[] selectionArgs = {UNREAD};

        Cursor cursor = database.query(
                DatabaseHelper2.TABLE_NOTIFICATION,  // The table name to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        return fetchData(cursor);
    }

    private ArrayList<NotificationModel> fetchData(Cursor cursor) {
        ArrayList<NotificationModel> notificationList = new ArrayList<>();


        if (cursor != null && cursor.getCount() > 0) {
            try {
                if (cursor.moveToFirst()) {
                    do {

                        String type = cursor.getString(cursor.getColumnIndex(DatabaseHelper2.KEY_TYPE));
                        String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper2.KEY_TITLE));
                        String message = cursor.getString(cursor.getColumnIndex(DatabaseHelper2.KEY_MESSAGE));
                        int productId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper2.KEY_PRODUCT_ID));
                        int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper2.KEY_ID));
                        String url = cursor.getString(cursor.getColumnIndex(DatabaseHelper2.KEY_URL));
                        String isRead = cursor.getString(cursor.getColumnIndex(DatabaseHelper2.KEY_IS_READ));

                        boolean isReadStatus = false;
                        if(isRead.equals(READ)) {
                            isReadStatus = true;
                        }


                        notificationList.add(new NotificationModel(id, type, title, message, productId, url, isReadStatus));
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return notificationList;
    }

    public void updateStatus(int itemId, boolean read) {

        String readStatus = UNREAD;
        if (read) {
            readStatus = READ;
        }

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper2.KEY_IS_READ, readStatus);

        // Which row to update, based on the ID
        String selection = DatabaseHelper2.KEY_ID + "=?";
        String[] selectionArgs = {String.valueOf(itemId)};

        database.update(
                DatabaseHelper2.TABLE_NOTIFICATION,
                values,
                selection,
                selectionArgs);
    }

    public void deleteAllNotification() {
        database.delete(DatabaseHelper2.TABLE_NOTIFICATION, null, null);
    }

    public int countNotification() {
        int numOfRows = (int) DatabaseUtils.queryNumEntries(database, DatabaseHelper2.TABLE_NOTIFICATION);
        return numOfRows;
    }

    private void dropNotificationTable() {
        String sql = "drop table " + DatabaseHelper2.TABLE_NOTIFICATION;
        try {
            database.execSQL(sql);
        } catch (SQLException e) {

        }
    }
}
