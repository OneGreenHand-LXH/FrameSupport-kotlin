package com.ogh.frame.base

/**
 * @dessribe 通用V层
 */
interface BaseView {
    fun showLoadingDialog(msg: String, isCancel: Boolean)
    fun dismissLoadingDialog()
}