package com.ogh.support.util

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.text.TextUtils
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.PermissionUtils
import com.ogh.frame.util.ToastUtil
import com.ogh.support.R
import com.ogh.support.service.DownloadService
import java.lang.Exception

object InstructionsUtils {
    /**
     * @param type   操作类别:  0、跳转本地 1、打开外部浏览器 2、通知栏下载APK或打开app 3、打开指定QQ 4、分享文本
     * @param action 跳转意图: 根据type变动，可能是本地activity或者浏览器之类的
     */
    fun jumpIntention(context: Context, type: Int, action: String) {
        when (type) {
            0 -> goLocationActivity(context, action)
            1 -> CommonUtil.intentToBrowsable(context, action)
            2 -> checkInstallOrDown(context, action)
            3 -> CommonUtil.contactQQ(context, action)
            4 -> if (!TextUtils.isEmpty(action)) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
                intent.type="text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, action)
                context.startActivity(Intent.createChooser(intent, "分享至"))
            }
        }
    }

    /**
     * 申请权限
     */
    private fun requestPermission(context: Context, url: String) {
        if (TextUtils.isEmpty(url)) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //android 10不需要权限
            download(context, url)
        } else {
            PermissionUtils.permission(PermissionConstants.STORAGE)
                .callback(object : PermissionUtils.SimpleCallback {
                    override fun onGranted() {
                        download(context, url)
                    }

                    override fun onDenied() {
                        ToastUtil.showShortToast("权限被拒绝,下载失败")
                    }
                })
                .request()
        }
    }

    /**
     * 通过service下载apk
     *
     * @param url 下载地址
     */
    fun download(context: Context, url: String) {
        val fileName = if (url.contains("/")) url.substring(url.lastIndexOf("/")).replace("/", "") else context.resources.getString(R.string.app_name) + System.currentTimeMillis() + ".apk"
        val intent = Intent(context, DownloadService::class.java)
        intent.putExtra("fileUrl", url)
        intent.putExtra("fileName", fileName)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //8.0以上
            context.startForegroundService(intent)
        } else
            context.startService(intent)
    }

    /**
     * 未安装则下载,已安装则打开
     *
     * @param url 里面可能包含包名和下载地址
     */
    fun checkInstallOrDown(context: Context, url: String) {
        if (TextUtils.isEmpty(url)) return
        if (url.contains("&")) { //说明含有包名(先检查是否安装，再决定是否下载)
            val packageName = url.split("&".toRegex()).toTypedArray()[0]
            val downUrl = url.split("&".toRegex()).toTypedArray()[1]
            CheckInstallTask(context, packageName, downUrl).execute()
        } else requestPermission(context, url)
    }

    /**
     * 主要用于后台控制跳转本地
     * intentUrl举例: TaskActivity?ID=1&NAME=小明  意思就是跳转到TaskDetailActivity，然后带了ID和NAME，两个参数
     */
    private fun goLocationActivity(context: Context, intentUrl: String) {
        if (TextUtils.isEmpty(intentUrl)) return
        try {
           //if (needlogin) {//如果需要登录
           //var intent = Intent()
           //intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
           //intent.setClass(context, getActivityClassName("LoginActivity")!!);
           //context.startActivity(intent);
          //} else {
            val intent = Intent(context, getActivityClassName(if (intentUrl.contains("@")) intentUrl.split("@".toRegex()).toTypedArray()[0] else intentUrl))
            intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
            if (intentUrl.contains("@")) { //说明带参数
                for (kv in intentUrl.split("@".toRegex()).toTypedArray()[1].split("\\$".toRegex()).toTypedArray()) { //拿到@后面的，然后对$分割处理
                    val k = kv.substring(0, kv.indexOf("=")) //拿到参数名
                    val v = kv.substring(kv.indexOf("=")).replaceFirst("=".toRegex(), "") //拿到参数
                    intent.putExtra(k, v)
                }
            }
            context.startActivity(intent)
            // }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 查找本地是否有这个class
     */
    private fun getActivityClassName(className: String): Class<*>? {
        return try {
            Class.forName("com.ogh.support.view.activity.$className") //TODO 这里需要改成自己对应activity位置
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 检测是否安装(异步任务)
     */
    private class CheckInstallTask(val context: Context, val packageName: String, val downUrl: String) : AsyncTask<Void?, Void?, Boolean?>() {

        override fun onPostExecute(s: Boolean?) {
            if (!s!!) //包名为空或者未安装就帮他下载
                requestPermission(context, downUrl) else  //包名不为空,并且已经安装就打开应用
                AppUtils.launchApp(packageName)
        }

        override fun doInBackground(vararg params: Void?): Boolean {
            return if (TextUtils.isEmpty(packageName)) false else AppUtils.isAppInstalled(packageName)
        }
    }
}