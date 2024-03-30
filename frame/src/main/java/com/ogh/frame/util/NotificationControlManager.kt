package com.ogh.frame.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Looper
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.ogh.frame.R
import com.ogh.frame.view.NotificationDialog
import java.util.concurrent.atomic.AtomicInteger

/**
 * 通知的管理类
 * example:
 * //发系统通知
 * NotificationControlManager.getInstance()?.notify("文件上传完成", "文件上传完成,请点击查看详情")
 * //发应用内通知
 * NotificationControlManager.getInstance()?.showNotificationDialog("文件上传完成","文件上传完成,请点击查看详情",
 *        object : NotificationControlManager.OnNotificationCallback {
 *             override fun onCallback() {
 *                Toast.makeText(this@MainActivity, "被点击了", Toast.LENGTH_SHORT).show()
 *              }
 * })
 */
class NotificationControlManager {
    private var autoIncrement = AtomicInteger(1001)
    private var dialog: NotificationDialog? = null

    companion object {
        const val channelId = "NotificationService"
        const val description = "通知消息"

        @Volatile
        private var sInstance: NotificationControlManager? = null

        @JvmStatic
        fun getInstance(): NotificationControlManager? {
            if (sInstance == null) {
                synchronized(NotificationControlManager::class.java) {
                    if (sInstance == null)
                        sInstance = NotificationControlManager()
                }
            }
            return sInstance
        }
    }

    /**
     * 是否打开通知
     */
    fun isOpenNotification(): Boolean {
        val currentActivity =
            ForegroundActivityManager.getInstance().getCurrentActivity() ?: return false
        val notificationManager: NotificationManagerCompat =
            NotificationManagerCompat.from(currentActivity)
        return notificationManager.areNotificationsEnabled()
    }

    /**
     * 跳转到系统设置页面去打开通知，注意在这之前应该有个Dialog提醒用户
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun openNotificationInSys() {
        val context = ForegroundActivityManager.getInstance().getCurrentActivity() ?: return
        val intent: Intent = Intent()
        try {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            //8.0及以后版本使用这两个extra.  >=API 26
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.applicationInfo.uid)
            //5.0-7.1 使用这两个extra.  <= API 25, >=API 21
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo.uid)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            //其他低版本或者异常情况，走该节点。进入APP设置界面
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.putExtra("package", context.packageName)
            //val uri = Uri.fromParts("package", packageName, null)
            //intent.data = uri
            context.startActivity(intent)
        }
    }

    /**
     * 发通知
     * @param title 标题
     * @param content 内容
     * @param cls 通知点击后跳转的Activity,默认为null跳转到MainActivity
     */
    fun notify(title: String, content: String, cls: Class<*>) {
        val context = ForegroundActivityManager.getInstance().getCurrentActivity() ?: return
        val notificationManager =
            context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        val builder: Notification.Builder
        val intent = Intent(context, cls)
        val pendingIntent: PendingIntent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true);
            notificationChannel.lightColor = Color.RED;
            notificationChannel.enableVibration(true);
            notificationChannel.vibrationPattern =
                longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager.createNotificationChannel(notificationChannel)
            builder = Notification.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(content)
        } else {
            builder = Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(content)
        }
        notificationManager.notify(autoIncrement.incrementAndGet(), builder.build())
    }

    /**
     * 显示应用内通知的Dialog,需要自己处理点击事件。listener默认为null,不处理也可以。dialog会在3000毫秒后自动消失
     * @param title 标题
     * @param content 内容
     * @param listener 点击的回调
     */
    fun showNotificationDialog(
        title: String,
        content: String,
        listener: OnNotificationCallback? = null
    ) {
        val activity = ForegroundActivityManager.getInstance().getCurrentActivity() ?: return
        dialog = NotificationDialog(activity, title, content)
        if (Thread.currentThread() != Looper.getMainLooper().thread) {   //子线程
            activity.runOnUiThread { showDialog(dialog, listener) }
        } else
            showDialog(dialog, listener)
    }

    /**
     * show dialog
     */
    private fun showDialog(dialog: NotificationDialog?, listener: OnNotificationCallback?) {
        dialog?.showDialogAutoDismiss()
        if (listener != null) {
            dialog?.setOnNotificationClickListener(object :
                NotificationDialog.OnNotificationClick {
                override fun onClick() = listener.onCallback()
            })
        }
    }

    /**
     * dismiss Dialog
     */
    fun dismissDialog() {
        if (dialog != null && dialog!!.isShowing)
            dialog!!.dismiss()
    }

    interface OnNotificationCallback {
        fun onCallback()
    }

}