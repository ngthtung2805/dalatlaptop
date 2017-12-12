package com.tungnui.abccomputer.data.sqlite

import com.tungnui.abccomputer.models.Cart
import com.tungnui.abccomputer.utils.ifNotNull
import org.jetbrains.anko.db.*

/**
 * Created by thanh on 04/12/2017.
 */
fun DatabaseHelper.addToCart(cart: Cart) {
    this.use {
        // check if product already exist
        cart.productId?.let {
            var qty = select("cart", "quantity")
                    .whereArgs("productId={id}", "id" to it)
                    .parseOpt(org.jetbrains.anko.db.IntParser)
            if (qty == null) {
                insert("cart",
                        "productId" to cart.productId,
                        "quantity" to cart.quantity,
                        "productName" to cart.productName,
                        "price" to cart.price,
                        "image" to cart.image,
                        "beforeDiscount" to cart.beforeDiscount,
                        "afterDiscount" to cart.afterDiscount,
                        "discount" to cart.discount)
            } else {
                update("cart", "quantity" to qty + 1)
                        .whereArgs("productId={proId}", "proId" to it).exec()
            }
        }
    }
}


fun DatabaseHelper.getAllCart(): List<Cart> = this.use {
    select("cart").exec { parseList(org.jetbrains.anko.db.classParser()) }
}

fun DatabaseHelper.clearAllCart() {
    this.use {
        delete("cart")
    }
}

fun DatabaseHelper.deleteCart(id: Int) {
    this.use {
        delete("cart", "id ={id}", "id" to id)
    }
}

fun DatabaseHelper.updateCartQuantity(cartId: Int, quantity: Int = 1) {
    this.use {
        update("cart", "quantity" to quantity).whereArgs("id = {cartId}", "cartId" to cartId).exec()
    }
}

fun DatabaseHelper.totalCart(): Int {
    var total = 0
    this.use {
        var cart = getAllCart()
        for (item in cart) {
            ifNotNull(item.price, item.quantity) { price, quantity ->
                total += price * quantity
            }
        }
    }
    return total
}

fun DatabaseHelper.totalCartItem(): Int {
    var total = 0
    this.use {
        var cart = getAllCart()
        for (item in cart) {
            ifNotNull(item.price, item.quantity) { price, quantity ->
                total += quantity
            }
        }
    }
    return total
}

fun DatabaseHelper.totalDiscount():Int{
    var total = 0
    this.use {
        var cart = getAllCart()
        for (item in cart) {
            item.discount?.let {
                total += it
            }
        }
    }
    return total
}

fun DatabaseHelper.totalAfterDiscountCart():Int{
    var total = 0
    this.use {
        var cart = getAllCart()
        for (item in cart) {
            ifNotNull(item.afterDiscount, item.quantity) { afterDiscount, quantity ->
                total += afterDiscount * quantity
            }
        }
    }
    return total
}
