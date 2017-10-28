package com.tungnui.dalatlaptop.interfaces

import com.travijuu.numberpicker.library.Enums.ActionEnum
import com.tungnui.dalatlaptop.models.Cart

interface CartRecyclerInterface {
    fun onProductDelete(cart: Cart)
    fun onProductSelect(productId: Int)
    fun onQuantityChange(cart: Cart, quantity: Int, actionEnum: ActionEnum)

}
