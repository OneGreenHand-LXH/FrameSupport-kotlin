package com.ogh.frame.util

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener

/**
 * Adapter子Item点击事件(防暴力点击)
 */
abstract class OnItemChildClickListener2 : OnItemChildClickListener {
    private var mLastClickTime = 0
    private var timeInterval = 1000L

    constructor(interval: Long) {
        timeInterval = interval
    }

    constructor()

    abstract fun onSingleClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int)

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val nowTime = System.currentTimeMillis()
        if (nowTime - mLastClickTime > timeInterval) { // 单次点击事件
            onSingleClick(adapter, view, position)
            mLastClickTime = nowTime.toInt()
        }
    }
}