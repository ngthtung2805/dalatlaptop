package com.tungnui.abccomputer.utils

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
import com.bumptech.glide.Glide

import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.data.constant.AppConstants
import com.tungnui.abccomputer.models.Image
import com.tungnui.abccomputer.models.MetaData
import com.tungnui.abccomputer.view.ResizableImageView
import com.tungnui.abccomputer.view.ResizableImageViewHeight
import java.text.DecimalFormat
import java.util.*
import java.util.regex.Pattern


/**
 * Created by thanh on 23/09/2017.
 */
fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun ResizableImageViewHeight.loadGlideImg(imageUrl: String?){
    Glide.with(context)
            .load(imageUrl)
            .placeholder(R.color.imgPlaceholder)
            .centerCrop()
            .into(this)
}
fun ImageView.loadGlideImg(imageUrl: String?){
    if(context == null)
        return
    Glide.with(context)
            .load(imageUrl)
            .placeholder(R.color.imgPlaceholder)
            .centerCrop()
            .into(this)
}

fun List<Image>.getFeaturedImage():Image{
    for(item in this){
        if(item.position == 0)
            return item
    }
    return Image()
}

fun List<MetaData>.getCapitalPrice():String{
    for(item in this){
        if(item.key=="wccaf_gi_vn")
            return item.value
    }
    return ""
}
fun String.formatPrice():String{
    val formatter = DecimalFormat("#,###")
    try {
        return formatter.format(this.toDouble()) + "đ"
    }catch (exception:Exception){
        return ""
    }

}
fun String.convertStatusToVN():String{
    return when(this){
        AppConstants.ORDER_STATUS_PENDING -> "Đang chờ xác nhận"
        AppConstants.ORDER_STATUS_COMPLETED ->"Đã hoàn tất"
        AppConstants.ORDER_STATUS_PROCESSING -> "Đang xử lý"
        AppConstants.ORDER_STATUS_CANCELLED -> "Đang hủy"
        AppConstants.ORDER_STATUS_REFUNDED ->"Chuyển hoàn"
        AppConstants.ORDER_STATUS_FAILED -> "Thất bại"
        else ->""
    }
}

fun String.parseDate(): String {
    try {
        val parts = this.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val year = parts[0]
        val month = parts[1]
        val dayTemp = parts[2]
        val parts2 = dayTemp.split("T".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val day = parts2[0]
        var time = parts2[1]
        return "$day/$month/$year $time"
    } catch (e: Exception) {
        return this
    }
}

fun String.convertToDate():Date?{
    //2017-12-15T00:00:00
    try {
        val parts = this.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var year = parts[0].toInt()
        var month = parts[1].toInt()
        var dayTemp = parts[2]
        var parts2 = dayTemp.split("T".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val day=parts2[0].toInt()
        return Date(year,month,day);
    }catch(e:Exception){
        return null
    }
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


//HideKeyboard
fun Activity.hideKeyboard():Boolean{
    val view= currentFocus
    view?.let{
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputMethodManager.hideSoftInputFromWindow(view.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
    }
    return false
}