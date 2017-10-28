package com.tungnui.dalatlaptop.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.tungnui.dalatlaptop.models.Cart
import org.jetbrains.anko.db.*

/**
 * Created by thanh on 23/10/2017.
 */
class CartDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "ShoppingCart", null, 1) {
    companion object {
        private var instance: CartDatabaseOpenHelper? = null
        @Synchronized
        fun getInstance(ctx: Context): CartDatabaseOpenHelper {
            if (instance == null) {
                instance = CartDatabaseOpenHelper(ctx.getApplicationContext())
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
                "image" to TEXT
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable("Cart", true)
    }


}

fun CartDatabaseOpenHelper.addToCart(cart: Cart) {
    this.use {
        // check if product already exist
        var quantity :Int?= 0
      cart.productId?.let{
          quantity = select("cart", "quantity")
                  .whereArgs("productId ={ID}", "ID" to it)
                  .exec { parseOpt(org.jetbrains.anko.db.IntParser) }
          quantity?.let{
              if(quantity==null){

              }
          }
      }

                cart.productId?.let {
            quantity = select("cart", "quantity").whereArgs(
                    "productId ={ID}", "ID" to it)
                    .exec { parseOpt(org.jetbrains.anko.db.IntParser) }
            if (quantity != null) {

            } else {

            }
        }
    }
}

fun CartDatabaseOpenHelper.getAll(): List<Cart> = this.use {
    select("cart").exec { parseList(org.jetbrains.anko.db.classParser()) }
}

fun CartDatabaseOpenHelper.deleteAll() {
    this.use {
        deleteAll()
    }
}

fun CartDatabaseOpenHelper.delete(id: Int) {
    this.use {
        delete("cart", "id ={id}", "id" to id)
    }
}

fun CartDatabaseOpenHelper.updateQuantity(cartId: Int, quantity: Int = 1) {
    this.use {
        update("cart", "quantity" to quantity).whereArgs("id = {cartId}", "cartId" to cartId).exec()
    }
}

fun CartDatabaseOpenHelper.total(): Int {
    var total = 0
    this.use {
        var cart = getAll()
        for (item in cart) {
            ifNotNull(item.price, item.quantity) { price, quantity ->
                total += price * quantity
            }
        }
    }
    return total
}

fun CartDatabaseOpenHelper.totalItem(): Int = this.getAll().count()

// Access property for Context
val Context.cartHelper: CartDatabaseOpenHelper
    get() = CartDatabaseOpenHelper.getInstance(getApplicationContext())
