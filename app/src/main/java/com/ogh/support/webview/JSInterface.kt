package com.ogh.support.webview

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.ogh.frame.FrameApplication.Companion.getContext
import com.ogh.frame.util.ToastUtil
import com.ogh.support.util.ChannelUtils
import com.ogh.support.view.dialog.TipDialog

/**
 * 备注: 在这里面的方法都是子线程
 */
class JSInterface(private val mContext: Context) {
    // 将请求成功的数据返回到主线程进行数据更新
    private val mHandler: Handler = Handler(getContext().mainLooper)

    //获取渠道号
    @JavascriptInterface
    fun getChannel(): String = ChannelUtils.getChannel()

    // 获取 App 版本号
    @JavascriptInterface
    fun getVersion(): String = AppUtils.getAppVersionName()

    //获取设备系统版本号
    @JavascriptInterface
    fun getOSVersion(): String = DeviceUtils.getSDKVersionName()

    //获取设备厂商
    @JavascriptInterface
    fun getManufacturer(): String = DeviceUtils.getManufacturer()

    //获取唯一设备ID
    @JavascriptInterface
    fun getDeviceId(): String = DeviceUtils.getUniqueDeviceId()

    //吐司
    @JavascriptInterface
    fun showToast(msg: String?) {
        mHandler.post { ToastUtil.showShortToast(msg) }
    }

    //弹框
    @JavascriptInterface
    fun showDialog(title: String, msg: String) {
        mHandler.post {
            val dialog = TipDialog(mContext)
            dialog.setCancel(false)
            dialog.setTitle(title)
            dialog.setContent(msg)
            dialog.show()
        }
    }

    //结束当前页面
    @JavascriptInterface
    fun doFinish() {
        if (mContext is Activity) mContext.finish()
    }
}