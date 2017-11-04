package com.tungnui.dalatlaptop.interfaces

import com.tungnui.dalatlaptop.models.Customer

/**
 * Interface declaring methods for login dialog.
 */
interface LoginDialogInterface {

    fun successfulLoginOrRegistration(customer: Customer)

}
