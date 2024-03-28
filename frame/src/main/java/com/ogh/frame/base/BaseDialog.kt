package com.ogh.frame.base

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.blankj.utilcode.util.ScreenUtils
import com.ogh.frame.R
import com.ogh.frame.util.CustomClickListener

/**
 * 通用dialog
 */
abstract class BaseDialog : Dialog, LifecycleObserver {
    private lateinit var mContext: Context
    private var mGravity = Gravity.CENTER
    private var mIsCancelable = true //点击外部是否可以取消弹框

    constructor(context: Context) : super(context, R.style.ActionSheetDialogStyle) {
        initCommon(context)
    }

    constructor(context: Context, themeResId: Int) : super(context, themeResId) {
        initCommon(context)
    }

    constructor(context: Context, gravity: Int, isCancelable: Boolean) : super(context, R.style.ActionSheetDialogStyle) {
        mGravity = gravity
        mIsCancelable = isCancelable
        initCommon(context)
    }

    private fun initCommon(context: Context) {
        mContext = context
        if (context is AppCompatActivity) {
            if (!context.isFinishing and !context.isDestroyed) //注册绑定生命周期
                context.lifecycle.addObserver(this)
        }
        setCancelable(mIsCancelable)
        setCanceledOnTouchOutside(mIsCancelable)
        setContentView(getLayoutID())
        val window = window //必须在setContentView()之后
        if (window != null) {
            val attributes = window.attributes
            attributes.width = getWidth()
            attributes.height = getHeight()
            attributes.gravity = mGravity
            window.attributes = attributes
        }
        initView(context)
    }

    override fun show() {
        if (mContext is Activity) {
            val activity = mContext as Activity
            if (activity.isFinishing or activity.isDestroyed) return
        }
        super.show()
    }

    open fun getWidth(): Int {
        return (ScreenUtils.getScreenWidth() * 0.9).toInt()
    }

    open fun getHeight(): Int {
        return ViewGroup.LayoutParams.WRAP_CONTENT
    }

    open fun initView(context: Context) {
    }

    abstract fun getLayoutID(): Int

    fun setOnClickListener(ids: IntArray, listener: CustomClickListener): BaseDialog {
        if (ids.isNotEmpty())
            for (id in ids) findViewById<View>(id).setOnClickListener(listener)
        return this
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        if (isShowing) dismiss()
    }

}