package com.ogh.support.util

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.ogh.support.R
import com.ogh.support.receiver.NotificationClickReceiver

/**
 * 下载通知栏适配(8.0用这个)
 */
class NotificationHelper(context: Context) {
    private var manager: NotificationManager? = null
    private val mContext: Context
    private val CHANNEL_ID = "DownloadService" //8.0两者必须一致
    private val CHANNEL_NAME = "下载更新" //会出现在(通知-类别）
    private val NOTIFICATION_ID = 1 //不能为0

    init {
        mContext = context.applicationContext
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            mChannel.description = "下载服务" //设置描述,会出现在(通知-类别-详细描述）
            //mChannel.enableLights(true)//设置提示灯
            //mChannel.lightColor=Color.RED;//设置提示灯颜色
            //mChannel.lockscreenVisibility=Notification.VISIBILITY_PUBLIC //设置锁屏可见 VISIBILITY_PUBLIC=可见
            mChannel.setShowBadge(true) //显示logo
            getManager().createNotificationChannel(mChannel)
        }
    }

    private fun getNotify(content: String): Notification.Builder {
        val builder = Notification.Builder(mContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) builder.setChannelId(CHANNEL_ID)
        builder.setContentTitle("应用下载") //设置标题
            .setContentText(content) //设置内容
            .setSmallIcon(R.mipmap.ic_launcher) //设置状态栏图标,一定要设置,否则报错(如果不设置它启动服务前台化不会报错,但是你会发现这个通知不会启动),如果是普通通知,不设置必然报错
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setWhen(System.currentTimeMillis()) //设置创建时间
            //.setLargeIcon(BitmapFactory.decodeResource(mContext.resources, R.mipmap.ic_launcher))//通知栏图片
            .setPriority(Notification.PRIORITY_HIGH)
        return builder
    }

    private fun  getManager(): NotificationManager {
        if (manager == null)
            manager= mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return manager!!
    }

    /**
     * 取消通知栏
     */
    fun cancel() {
        getManager().cancel(NOTIFICATION_ID)
    }

    /**
     * 显示通知栏
     */
    fun showNotification() {
        getManager().notify(NOTIFICATION_ID, getNotify("准备下载").build())
    }

    /**
     * 获取 Notification
     */
    fun getNotification(): Notification {
        return getNotify("准备下载").build()
    }

    /**
     * 不断调用次方法通知通知栏更新进度条
     */
    fun updateProgress(progress: Int) {
        val builder = if (progress == -1) getNotify("下载失败") else getNotify(mContext.getString(
                R.string.update_download_progress,
                progress
            )).setProgress(100, progress, false)
        getManager().notify(NOTIFICATION_ID, builder.build())
    }

    /**
     * 下载完成后，设置通知栏可以点击
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    fun downloadComplete(fileName: String?) {
        val intentClick = Intent(mContext, NotificationClickReceiver::class.java)
        intentClick.action = "NOTIFICATION_CLICKED"
        intentClick.putExtra("FILE_NAME", fileName)
        val pendingIntentClick =
            PendingIntent.getBroadcast(mContext, 0, intentClick, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = getNotify("下载已完成").setContentIntent(pendingIntentClick)
        getManager().notify(NOTIFICATION_ID, builder.build())
    }
}