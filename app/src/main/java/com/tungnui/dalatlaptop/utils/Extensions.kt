package com.tungnui.dalatlaptop.utils

import android.app.Activity
import android.content.Context
import android.support.annotation.IdRes
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.tungnui.dalatlaptop.views.ResizableImageViewHeight
import com.squareup.picasso.Picasso
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.libraryhelper.Utils
import com.tungnui.dalatlaptop.models.Image
import java.text.DecimalFormat
import java.util.regex.Pattern


/**
 * Created by thanh on 23/09/2017.
 */
fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun ImageView.loadImg(imageUrl: String?) {
    Picasso.with(context).load(imageUrl)
            .fit().centerInside()
            .placeholder(R.drawable.placeholder_loading)
            .error(R.drawable.placeholder_error)
            .into(this)
}
fun ResizableImageViewHeight.loadImg(imageUrl: String?){
    Picasso.with(context)
            .load(imageUrl)
            .fit().centerInside()
            .placeholder(R.drawable.placeholder_loading)
            .error(R.drawable.placeholder_error)
            .into(this)
}
fun List<Image>.getFeaturedImage():Image{
    for(item in this){
        if(item.position == 0)
            return item
    }
    return Image()
}
fun String.formatPrice():String{
    val formatter = DecimalFormat("#,###")
    return formatter.format(this.toDouble()) + "đ"
}
/*
* GET NEXT URL FROM LINK IN HEADER
* */
fun String.getNextUrl(): String? {
    val urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)"
    val pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE)
    val urlMatcher = pattern.matcher(this)
    val result = mutableListOf<String>()
    while (urlMatcher.find()) {
        result.add(this.substring(urlMatcher.start(0),urlMatcher.end(0)))
    }
    return when(result.count()){
        1->if(this.contains("rel=\"next\"")) result[0].replace("%5B0%5D","") else null
        else-> result[1].replace("%5B0%5D","")
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

/**
 * Runs a FragmentTransaction, then calls commit().
 */
private inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        action()
    }.commit()
}

fun <T1, T2> ifNotNull(value1: T1?, value2: T2?, bothNotNull: (T1, T2) -> (Unit)) {
    if (value1 != null && value2 != null) {
        bothNotNull(value1, value2)
    }
}

//TExt input
fun TextInputLayout.getTextFromInputLayout(): String {
    return this.editText?.text.toString()
}

fun TextInputLayout.setTextToInputLayout(text: String) {
    if (this.editText != null) {
        this.editText?.setText(text)
    }
}

fun TextInputLayout.isVaildForEmail():Boolean{
    val input = editText?.text.toString()
    if(input.isNullOrBlank()){
        error = resources.getString(R.string.required)
        return false
    }
    return false
}
fun TextInputLayout.checkTextInputLayoutValueRequirement(errorValue: String): Boolean {
    if (this.editText != null) {
        val text = Utils.getTextFromInputLayout(this)
        if (text == null || text.isEmpty()) {
            this.isErrorEnabled = true
            this.error = errorValue
            return false
        } else {
            this.isErrorEnabled = false
            return true
        }
    }
    return false
}

//HideKeyboard
fun Activity.hideKeyboard():Boolean{
    val view= currentFocus
    view?.let{
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputMethodManager.hideSoftInputFromWindow(view.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
    }
    return false
}