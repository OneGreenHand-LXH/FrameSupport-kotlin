package com.ogh.support.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationManagerCompat
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.PermissionUtils
import com.ogh.frame.util.ToastUtil
import com.ogh.support.view.dialog.TipDialog
import com.ogh.support.widget.HtmlParser
import java.lang.Exception
import kotlin.math.min

/**
 * 通用工具类
 */
object CommonUtil {
    /**
     * 请求权限弹框
     */
    fun getPermissions(context: Context, tips: String) {
        val dialog = TipDialog(context)
        dialog.setContent(if (TextUtils.isEmpty(tips)) "为了提供更好的服务,请授权APP必要的权限!单击【确定】按钮前往设置中心授权权限" else tips)
        dialog.setCancel(false)
        dialog.setOnSureClick(object : TipDialog.SureCalk {
            override fun onSureClick(view: View) {
                PermissionUtils.launchAppDetailsSettings()
            }
        })
        dialog.show()
    }

    /**
     * 请求打开通知权限
     */
    fun notificationAuthority(context: Context): Boolean {
        val manager: NotificationManagerCompat = NotificationManagerCompat.from(context)
        val isOpened: Boolean = manager.areNotificationsEnabled() //API 19以上也可以用这个判断
        if (!isOpened) { //没有授予通知权限
            val dialog = TipDialog(context)
            dialog.setContent("系统通知未开启,将不能及时收到消息通知,请前往开启")
            dialog.setOnSureClick(object :TipDialog.SureCalk{
                override fun onSureClick(view: View) {
                    val intent = Intent()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // android 8.0引导
                        intent.action="android.settings.APP_NOTIFICATION_SETTINGS"
                        intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  // android 5.0-7.0
                        intent.action="android.settings.APP_NOTIFICATION_SETTINGS"
                        intent.putExtra("app_package", context.packageName)
                        intent.putExtra("app_uid", context.applicationInfo.uid)
                    } else {   // 其他
                        intent.action="android.settings.APPLICATION_DETAILS_SETTINGS"
                        intent.data=Uri.fromParts("package", context.packageName, null)
                    }
                    intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }
            })
            dialog.show()
        }
        return isOpened
    }

    /**
     * 设置支持html标签
     * String s = "<font color='#999999'>温馨提示</font>";
     */
    fun setHtmlColor(string: String?): Spanned {
        return if (TextUtils.isEmpty(string)) Html.fromHtml("") else {
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(string, Html.FROM_HTML_MODE_COMPACT) else Html.fromHtml(string)
        }
    }

    /**
     * 设置支持html标签（额外支持文字大小、删除线,单位为px）
     * String s = "<font color='#999999' size='20px'>温馨提示</font>";
     */
    fun setHtmlColor2(string: String): Spanned {
        return HtmlParser().buildSpannedText(string)
    }

    /**
     * 版本号比较(versionName对比方式)
     *
     * @return 0代表相等，1代表version1大于version2，-1代表version1小于version2
     */
    fun compareVersion(version1: String, version2: String): Int {
        if (TextUtils.isEmpty(version1) || TextUtils.isEmpty(version2) || version1 == version2) return 0
        val version1Array = version1.split("\\.".toRegex()).toTypedArray()
        val version2Array = version2.split("\\.".toRegex()).toTypedArray()
        var index = 0
        val minLen = min(version1Array.size, version2Array.size) // 获取最小长度值
        var diff = 0
        // 循环判断每位的大小
        while (index < minLen && version1Array[index].toInt() - version2Array[index].toInt().also { diff = it } == 0) index++
        return if (diff == 0) {
            for (i in index until version1Array.size) { // 如果位数不一致，比较多余位数
                if (version1Array[i].toInt() > 0) return 1
            }
            for (i in index until version2Array.size) {
                if (version2Array[i].toInt() > 0) return -1
            }
           return 0
        } else if (diff > 0) 1 else -1
    }

    /**
     * 手机号用****号隐藏中间数字
     */
    fun setTingPhone(phone: String): String {
        return if (TextUtils.isEmpty(phone)) "" else phone.replace("(\\d{3})\\d{4}(\\d{4})".toRegex(), "$1****$2")
    }

    /**
     * 第一个汉字用*代替
     */
    fun getFirstHindName(name: String): String {
        return if (TextUtils.isEmpty(name)) "" else name.replaceFirst(name.substring(0, 1).toRegex(), "*")
    }

    /**
     * 邮箱用****号隐藏前面的字母
     */
    fun setTingEmail(email: String): String {
        return if (TextUtils.isEmpty(email)) "" else email.replace("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)".toRegex(), "$1****$3$4")
    }

    /**
     * 设置页面的透明度
     *
     * @param bgAlpha 1表示不透明
     */
    fun setBackgroundAlpha(activity: Activity, bgAlpha: Float) {
        val lp: WindowManager.LayoutParams = activity.window.attributes
        lp.alpha = bgAlpha
        if (bgAlpha == 1f) activity.window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND) //不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        else activity.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND) //此行代码主要是解决在华为手机上半透明效果无效的bug
        activity.window.attributes=lp
    }

    //跳转到浏览器
    fun intentToBrowsable(mContext: Context, url: String) {
        if (TextUtils.isEmpty(url)) return
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data=Uri.parse(url)
            mContext.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtil.showShortToast("浏览器打开失败")
        }
    }

    /**
     * 跳转QQ
     */
    fun contactQQ(context: Context, qq: String) {
        val url: String
        val q = if (TextUtils.isEmpty(qq)) "40012345" else qq
        url = if (AppUtils.isAppInstalled("com.tencent.mobileqq") || AppUtils.isAppInstalled("com.tencent.tim")) "mqqwpa://im/chat?chat_type=wpa&uin=$q" else "http://wpa.qq.com/msgrd?v=3&uin=$q&site=qq&menu=yes"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    //检测GPS是否打开
    fun checkGpsIsOpen(mContext: Context): Boolean {
        val locationManager: LocationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    //跳转GPS设置界面
    fun intentToOpenGps(activity: Activity, requestCode: Int) {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivityForResult(intent, requestCode)
    }
}