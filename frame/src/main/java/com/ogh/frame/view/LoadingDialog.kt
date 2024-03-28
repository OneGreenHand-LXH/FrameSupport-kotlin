package com.ogh.frame.view

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import com.ogh.frame.R
import com.ogh.frame.base.BaseDialog

/**
 * 加载框
 */
class LoadingDialog(context: Context) : BaseDialog(context, R.style.DialogStyle) {
    private lateinit var loadingText: TextView

    override fun initView(context: Context) {
        loadingText = findViewById(R.id.loading_text)
    }

    override fun getLayoutID(): Int {
        return R.layout.dialog_loading
    }

    override fun getWidth(): Int {
        return ViewGroup.LayoutParams.WRAP_CONTENT
    }

    fun setMsg(msg: String) {
        loadingText.text = msg
    }

    fun setCancel(isCancel: Boolean) {
        setCancelable(isCancel)
        setCanceledOnTouchOutside(isCancel)
    }

}