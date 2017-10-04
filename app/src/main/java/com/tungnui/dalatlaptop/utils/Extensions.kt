package com.tungnui.dalatlaptop.utils

import android.support.annotation.IdRes
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.tungnui.dalatlaptop.R

/**
 * Created by thanh on 23/09/2017.
 */
fun ViewGroup.inflate(layoutId:Int, attachToRoot:Boolean=false): View {
    return LayoutInflater.from(context).inflate(layoutId,this,attachToRoot)
}

fun ImageView.loadImg(imageUrl: String?) {
        if (TextUtils.isEmpty(imageUrl) || imageUrl == null) {
                Picasso.with(context).load(R.mipmap.ic_launcher).into(this)
           } else {
                Picasso.with(context).load(imageUrl).into(this)
            }
    }

fun View.showSnackBar(message: String, duration: Int) {
    Snackbar.make(this, message, duration).show()
}
//Appcompat extension
fun AppCompatActivity.replaceFragmentInActivity(fragment: Fragment, @IdRes frameId: Int) {
    supportFragmentManager.transact {
        replace(frameId, fragment)
    }
}

/**
 * The `fragment` is added to the container view with tag. The operation is
 * performed by the `fragmentManager`.
 */
fun AppCompatActivity.addFragmentToActivity(fragment: Fragment, tag: String) {
    supportFragmentManager.transact {
        add(fragment, tag)
    }
}

fun AppCompatActivity.setupActionBar(@IdRes toolbarId: Int, action: ActionBar.() -> Unit) {
    setSupportActionBar(findViewById(toolbarId))
    supportActionBar?.run {
        action()
    }
}

/**
 * Runs a FragmentTransaction, then calls commit().
 */
private inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        action()
    }.commit()
}