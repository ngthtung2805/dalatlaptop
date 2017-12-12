package com.tungnui.abccomputer.data.sqlite

import com.tungnui.abccomputer.models.Product
import com.tungnui.abccomputer.utils.getFeaturedImage
import org.jetbrains.anko.db.*

/**
 * Created by thanh on 04/12/2017.
 */
fun DatabaseHelper.insertWishItem(product: Product?) {
    this.use {
        // check if product already exist
        product?.id?.let {
                insert("wishlist",
                        "productId" to it,
                        "productName" to product.name,
                        "price" to product.price,
                        "image" to product.images?.getFeaturedImage())
            }
        }
}


fun DatabaseHelper.deleteWishItemById(id: Int) {
    this.use {
        delete("wishlist", "productId ={id}", "id" to id)
    }
}

fun DatabaseHelper.isAlreadyWished(id:Int):Boolean{
    return this.use {
        val productId = select("cart", "productId")
                .whereArgs("productId={id}", "id" to id)
                .parseOpt(org.jetbrains.anko.db.IntParser)
        if(productId == null) false else true
    }
}