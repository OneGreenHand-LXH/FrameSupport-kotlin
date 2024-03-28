package com.ogh.support.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import com.ogh.support.widget.Solve7PopupWindow

/**
 * Popupwindow工具类，直接传入view然后调用显示即可
 */
object PopupWindowUtil {
    /**
     * @param layout  布局
     * @param isMatch 宽度是否MATCH_PARENT
     * @param cancel  是否可以点击取消
     * @param isAlpha 是否背景半透明
     */
    fun getPopupWindow(context: Context, layout: View, isMatch: Boolean, cancel: Boolean, isAlpha: Boolean): Solve7PopupWindow {
        val popupWindow = Solve7PopupWindow()
        popupWindow.contentView = layout
        popupWindow.width =
        if (isMatch) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
        popupWindow.height = ViewGroup.LayoutParams.WRAP_CONTENT
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.isOutsideTouchable = cancel // 设置popupwindow外部可点击
        popupWindow.inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED //让pop覆盖在输入法上面
        popupWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE //软键盘顶起当前界面,当前布局的高度会发生变化
        if (isAlpha) {
            CommonUtil.setBackgroundAlpha(context as Activity, 0.8f) //显示阴影
            popupWindow.setOnDismissListener { CommonUtil.setBackgroundAlpha(context, 1.0f) }
        }
        return popupWindow
    }
}