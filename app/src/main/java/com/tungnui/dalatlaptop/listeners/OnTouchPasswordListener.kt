package com.tungnui.dalatlaptop.listeners

import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText

/**
 * Adds show/hide functionality to editText with passwordType.
 */
class OnTouchPasswordListener
/**
 * Adds show/hide functionality to editText with passwordType.
 *
 * @param passwordET EditText with defined inputType.
 */
(private val passwordET: EditText) : View.OnTouchListener {

    private var mPreviousInputType: Int = 0

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val action = event.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mPreviousInputType = passwordET.inputType
                setInputType(EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD, true)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                setInputType(mPreviousInputType, true)
                mPreviousInputType = -1
            }
        }

        return false
    }

    private fun setInputType(inputType: Int, keepState: Boolean) {
        var selectionStart = -1
        var selectionEnd = -1
        if (keepState) {
            selectionStart = passwordET.selectionStart
            selectionEnd = passwordET.selectionEnd
        }
        passwordET.inputType = inputType
        if (keepState) {
            passwordET.setSelection(selectionStart, selectionEnd)
        }
    }
}
