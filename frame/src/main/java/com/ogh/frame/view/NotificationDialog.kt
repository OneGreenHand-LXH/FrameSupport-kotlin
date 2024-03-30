package com.ogh.frame.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.TextView
import com.ogh.frame.R

/**
 * 通知的自定义Dialog
 */
class NotificationDialog(context: Context, var title: String, var content: String) :
    Dialog(context, R.style.DialogNotificationTop) {

    private var mListener: OnNotificationClick? = null
    private var mStartY: Float = 0F
    private var mView: View? = null
    private var mHeight: Int = 0

    init {
        mView = LayoutInflater.from(context).inflate(R.layout.common_layout_notifacation, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mView!!)
        window?.setGravity(Gravity.TOP)
        val layoutParams = window?.attributes
        layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams?.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        window?.attributes = layoutParams
        window?.setWindowAnimations(R.style.DialogNotificationAnimation)
        //按空白处不能取消
        setCanceledOnTouchOutside(false)
        //初始化界面数据
        initData()
    }

    private fun initData() {
        val tvTitle = findViewById<TextView>(R.id.tv_title)
        val tvContent = findViewById<TextView>(R.id.tv_content)
        if (title.isNotEmpty())
            tvTitle.text = title
        if (content.isNotEmpty())
            tvContent.text = content
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isOutOfBounds(event))
                    mStartY = event.y
            }
            MotionEvent.ACTION_UP -> {
                if (mStartY > 0) {
                    val moveY = event.y
                    if (mStartY - moveY >= getViewHalfHeight()) {//滑动超过控件的一半认定为滑动事件
                        dismiss()
                    } else if (mStartY == moveY && isOutOfBounds(event)) {//认定为点击事件
                        mListener?.onClick()
                        dismiss()
                    }
                }
            }
        }
        return false
    }

    /**
     * 点击是否在范围外
     */
    private fun isOutOfBounds(event: MotionEvent): Boolean {
        val yValue = event.y
        return yValue > 0 && yValue <= if (mHeight == 0) 50 else mHeight
    }

    /**
     * 是否有控件一半的高度
     * 没有就默认50
     */
    private fun getViewHalfHeight(): Int = if (mHeight == 0) 50 else mHeight / 2

    private fun setDialogSize() {
        mView?.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            mHeight = v?.height ?: 50
        }
    }

    /**
     * 显示Dialog但是不会自动退出
     */
    fun showDialog() {
        if (!isShowing) {
            show()
            setDialogSize()
        }
    }

    /**
     * 显示Dialog,3000毫秒后自动退出
     */
    fun showDialogAutoDismiss() {
        if (!isShowing) {
            show()
            setDialogSize()
            //延迟3000毫秒后自动消失
            Handler(Looper.getMainLooper()).postDelayed({
                if (isShowing)
                    dismiss()
            }, 3000L)
        }
    }

    //处理通知的点击事件
    fun setOnNotificationClickListener(listener: OnNotificationClick) {
        mListener = listener
    }

    interface OnNotificationClick {
        fun onClick()
    }
}