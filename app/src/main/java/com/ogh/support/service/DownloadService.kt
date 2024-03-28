package com.ogh.support.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.text.TextUtils
import com.arialyy.annotations.Download
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.task.DownloadTask
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.FileUtils
import com.ogh.frame.util.ToastUtil
import com.ogh.support.config.AppConfig
import com.ogh.support.util.NotificationHelper
import java.lang.Exception

/**
 * 下载服务
 */
class DownloadService : Service() {
    private var fileName: String? = null//文件名
    private var notificationHelper: NotificationHelper? = null
    private var isDownloading = false //是否在下载中

    private fun getNotification(): NotificationHelper {
        if (notificationHelper == null)
            notificationHelper = NotificationHelper(applicationContext)
        return notificationHelper!!
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForeground(1, getNotification().getNotification()) //服务前台化只能用startForeground()方法,不可用notificationManager.notify(1,notification);,不然报错
        else
            getNotification().showNotification() //创建通知栏
        Aria.download(this).register()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        initServer(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * 初始化下载相关东西
     */
    private fun initServer(intent: Intent?) {
        if (intent == null) {
            stopSelf()
            return
        }
        val fileUrl : String? = intent.getStringExtra("fileUrl");//下载的文件地址
        fileName = intent.getStringExtra("fileName")
        if (TextUtils.isEmpty(fileUrl) || TextUtils.isEmpty(fileName) || !FileUtils.createOrExistsDir(AppConfig.FilePath.FILE_FOLDER)) {
            error()
            return
        }
        if (!isDownloading) { //没有在下载中才可以下载
            if (FileUtils.isFileExists(AppConfig.FilePath.FILE_FOLDER + fileName)) {  //如果更新之前存在apk就直接安装
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) //未没有绑定service,手动取消通知栏
                    getNotification().cancel()
                AppUtils.installApp(AppConfig.FilePath.FILE_FOLDER + fileName)
                stopSelf() //手动停止服务(8.0同时通知栏会消失)
            } else {
                try {
                    Aria.download(this)
                        .load(fileUrl)
                        .setFilePath(AppConfig.FilePath.FILE_FOLDER + fileName)
                        .ignoreCheckPermissions()
                        .ignoreFilePathOccupy()
                        .create()
                } catch (e: Exception) { //有可能下载地址错误
                    e.printStackTrace()
                    error()
                    ToastUtil.showShortToast("下载失败")
                }
            }
        } else ToastUtil.showShortToast("已在下载中")
    }

    override fun onDestroy() {
        super.onDestroy()
        Aria.download(this).unRegister()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @Download.onPre
    fun onTaskPre(task: DownloadTask) {
        isDownloading = true
    }

    @Download.onTaskCancel
    fun onTaskCancel(task: DownloadTask) {
        error()
    }

    @Download.onTaskFail
    fun onTaskFail(task: DownloadTask) {
        error()
    }

    @Download.onTaskComplete
    fun onTaskComplete(task: DownloadTask) {
        getNotification().downloadComplete(fileName) //手动设置下载完成,并且设置通知栏点击事件(不然进度显示有问题)
        isDownloading = false
        if (FileUtils.isFileExists(AppConfig.FilePath.FILE_FOLDER + fileName)) //如果本地存在文件，直接调用安装操作
            AppUtils.installApp(AppConfig.FilePath.FILE_FOLDER + fileName)
        stopSelf()
    }

    @Download.onTaskRunning
    fun onTaskRunning(task: DownloadTask) {
        if (task.fileSize == 0L) {
            getNotification().updateProgress(-1)
        } else
            getNotification().updateProgress(task.percent)
    }

    private fun error() {
        getNotification().updateProgress(-1)
        isDownloading = false
        // stopSelf();
    }
}