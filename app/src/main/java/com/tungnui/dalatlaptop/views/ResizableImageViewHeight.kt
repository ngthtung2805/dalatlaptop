package com.tungnui.dalatlaptop.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView

class ResizableImageViewHeight(context: Context, attrs: AttributeSet) : ImageView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val d = drawable

        if (d != null) {
            // ceil not round - avoid thin vertical gaps along the left/right edges
            val height = MeasureSpec.getSize(heightMeasureSpec)
            val width = Math.ceil((height.toFloat() * d.intrinsicWidth.toFloat() / d.intrinsicHeight.toFloat()).toDouble()).toInt()
            setMeasuredDimension(width, height)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

}
