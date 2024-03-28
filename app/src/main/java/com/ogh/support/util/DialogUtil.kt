package com.ogh.support.util

import android.app.*
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import com.ogh.frame.R

/**
 * Dialog工具类，直接传入view,然后调用show方法即可
 */
object DialogUtil {

    fun getDialog(context: Context, view: Int): Dialog {
        return getDialog(context, view, Gravity.CENTER)
    }

    fun getDialog(context: Context, view: Int, location: Int): Dialog {
        return getDialog(context, view, location, true)
    }

    fun getDialog(context: Context, view: Int, location: Int, cancel: Boolean): Dialog {
        val dialog = Dialog(context, R.style.ActionSheetDialogStyle)
        dialog.setCancelable(cancel)
        dialog.setCanceledOnTouchOutside(cancel)
        dialog.setContentView(view)
        val window = dialog.window
        if (window != null) {
            val p = window.attributes
            p.width = ViewGroup.LayoutParams.MATCH_PARENT
            p.height = ViewGroup.LayoutParams.WRAP_CONTENT
            window.attributes = p
            window.setGravity(location)
        }
        return dialog
    }
}