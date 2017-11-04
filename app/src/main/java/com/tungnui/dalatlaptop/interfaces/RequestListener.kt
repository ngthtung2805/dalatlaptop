package com.tungnui.dalatlaptop.interfaces

import com.android.volley.VolleyError

interface RequestListener {
    fun requestSuccess(newId: Long)
    fun requestFailed(error: VolleyError)
}

