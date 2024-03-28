package com.ogh.support.widget

import android.graphics.Rect
import android.os.Build
import android.view.View
import android.widget.PopupWindow

/**
 * @describe 解决部分手机showAsDropDown位置不对问题
 */
class Solve7PopupWindow : PopupWindow {

    constructor(mMenuView: View, matchParent: Int, matchParent1: Int) : super(mMenuView, matchParent, matchParent1)

    constructor() {
        isClippingEnabled = false //PopupWindow的阴影没有覆盖状态栏的问题
    }

    override fun showAsDropDown(anchor: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val rect = Rect()
            anchor.getGlobalVisibleRect(rect)
            val h = anchor.resources.displayMetrics.heightPixels - rect.bottom
            height = h
        }
        super.showAsDropDown(anchor)
    }

    override fun showAsDropDown(anchor: View, xoff: Int, yoff: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val rect = Rect()
            anchor.getGlobalVisibleRect(rect)
            val h = anchor.resources.displayMetrics.heightPixels - rect.bottom + Math.abs(yoff)
            height = h
        }
        super.showAsDropDown(anchor, xoff, yoff)
    }

    override fun showAsDropDown(anchor: View, xoff: Int, yoff: Int, gravity: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val rect = Rect()
            anchor.getGlobalVisibleRect(rect)
            val h = anchor.resources.displayMetrics.heightPixels - rect.bottom + Math.abs(yoff)
            height = h
        }
        super.showAsDropDown(anchor, xoff, yoff, gravity)
    }
}