package com.ogh.support.view.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import com.ogh.frame.base.BaseDialog
import com.ogh.support.R
import com.ogh.support.util.CommonUtil

/**
 * 通用提示框
 */
class TipDialog(context: Context) : BaseDialog(context) {
    lateinit var title: TextView
    lateinit var content: TextView
    lateinit var cancel: TextView
    lateinit var sure: TextView
    private var sureCalk: SureCalk? = null
    private var cancelCalk: CancelCalk? = null

    fun setOnSureClick(sureCalk: SureCalk) {
        this.sureCalk = sureCalk
    }

    fun setOnCancelClick(sureCalk: CancelCalk) {
        cancelCalk = sureCalk
    }

    interface SureCalk {
        fun onSureClick(view: View)
    }

    interface CancelCalk {
        fun onCancelClick(view: View)
    }

    override fun initView(context: Context) {
        title = findViewById(R.id.title)
        content = findViewById(R.id.content)
        cancel = findViewById(R.id.cancel)
        sure = findViewById(R.id.sure)
        cancel.setOnClickListener {
            cancelCalk?.onCancelClick(cancel)
            dismiss()
        }
        sure.setOnClickListener{
            sureCalk?.onSureClick(sure)
            dismiss()
        }
    }

//    @Override
//    override fun show() {
//        content.viewTreeObserver.addOnGlobalLayoutListener(listener);
//        super.show();
//    }

    private val listener: ViewTreeObserver.OnGlobalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            @SuppressLint("RtlHardcoded")
            override fun onGlobalLayout() {
                content.gravity =if(content.lineCount > 1) Gravity.LEFT else Gravity.CENTER
                content.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }

    /**
     * 是否可以点击取消
     */
    fun setCancel(canCancel: Boolean) {
        setCancelable(canCancel)
        setCanceledOnTouchOutside(canCancel)
        cancel.visibility = if (canCancel) View.VISIBLE else View.GONE
    }

    fun setTitle(msg: String) {
        title.text = msg
    }

    fun setColorTitle(msg: String) {
        title.text = CommonUtil.setHtmlColor(msg)
    }

    fun setContent(msg: String) {
        content.text = msg
    }

    fun setColorContent(msg: String) {
        content.text = CommonUtil.setHtmlColor(msg)
    }

    fun setSureText(msg: String) {
        sure.text = msg
    }

    fun setSureColorText(msg: String) {
        sure.text = CommonUtil.setHtmlColor(msg)
    }

    fun setCancelText(msg: String) {
        cancel.text = msg
    }

    fun setCancelColorText(msg: String) {
        cancel.text = CommonUtil.setHtmlColor(msg)
    }

    override fun getLayoutID(): Int {
        return R.layout.dialog_tips
    }
}