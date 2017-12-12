package com.tungnui.abccomputer.listener

import com.tungnui.abccomputer.models.Customer

/**
 * Created by ashiq on 8/27/2017.
 */

interface LoginListener {

    fun onLoginResponse(customer: Customer)
    fun onRegisterResponse(customer:Customer)
    fun onLoginResponse(id:Int)

}
