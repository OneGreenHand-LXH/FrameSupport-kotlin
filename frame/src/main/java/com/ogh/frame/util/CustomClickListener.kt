package com.ogh.frame.util

import android.view.View

/**
 * @describe 通用点击事件(防止暴力点击)
 */
abstract class CustomClickListener : View.OnClickListener {
    private var mLastClickTime = 0L
    private var timeInterval = 1000L

    constructor(interval: Long) {
        timeInterval = interval
    }

    constructor()

    abstract fun onSingleClick(v: View)

    override fun onClick(v: View) {
        val nowTime = System.currentTimeMillis()
        if (nowTime - mLastClickTime > timeInterval) { // 单次点击事件
            onSingleClick(v)
            mLastClickTime = nowTime
        }
    }
}