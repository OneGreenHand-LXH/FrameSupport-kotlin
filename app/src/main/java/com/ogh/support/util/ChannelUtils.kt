package com.ogh.support.util

import android.content.pm.PackageManager
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.Utils

object ChannelUtils {

    fun getChannel(): String {
//        val channel = WalleChannelReader.get(FrameApplication.getContext(), "CHANNEL");//通过Walle获取到的渠道号,需要手动注入渠道信息,不然为空
//        return if(TextUtils.isEmpty(channel)) "MeiZu" else channel!!;
       return getMate("channel")
    }

    /**
     * 动态获取(AndroidManifest文件中channel字段)
     */
    private fun getMate(key: String): String {
        var result = "MeiZu"
        try {
            val appInfo = Utils.getApp().packageManager.getApplicationInfo(AppUtils.getAppPackageName(), PackageManager.GET_META_DATA)
            result = appInfo.metaData.getString(key).toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}