package com.tungnui.abccomputer.data.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

/**
 * Created by thanh on 23/10/2017.
 */
class DatabaseHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "abcDB", null, 1) {
    companion object {
        private var instance: DatabaseHelper? = null
        @Synchronized
        fun getInstance(ctx: Context): DatabaseHelper {
            if (instance == null) {
                instance = DatabaseHelper(ctx.getApplicationContext())
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Here you create tables
        db.createTable(
                "cart", true,
                "id" to INTEGER + PRIMARY_KEY + UNIQUE,
                "productId" to INTEGER,
                "productName" to TEXT,
                "quantity" to INTEGER,
                "price" to INTEGER,
                "image" to TEXT,
                "beforeDiscount" to INTEGER,
                "afterDiscount" to INTEGER,
                "discount" to INTEGER
        )
        db.createTable(
                "wishlist",true,
                "productId" to INTEGER + PRIMARY_KEY+ UNIQUE,
                "productName" to TEXT,
                "price" to INTEGER,
                "image" to TEXT
        )

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable("cart", true)
        db.dropTable("wishlist",true)
    }
}
// Access property for Context
val Context.DbHelper: DatabaseHelper
    get() = DatabaseHelper.getInstance(getApplicationContext())



