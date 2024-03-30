package com.ogh.frame.util

import android.app.Activity
import java.lang.ref.WeakReference

/**
 * 前台Activity管理类
 */
class ForegroundActivityManager {

    private var currentActivityWeakRef: WeakReference<Activity>? = null

    companion object {
        private val instance = ForegroundActivityManager()

        @JvmStatic
        fun getInstance(): ForegroundActivityManager {
            return instance
        }
    }

    fun getCurrentActivity(): Activity? {
        var currentActivity: Activity? = null
        if (currentActivityWeakRef != null)
            currentActivity = currentActivityWeakRef?.get()
        return currentActivity
    }

    fun setCurrentActivity(activity: Activity) {
        currentActivityWeakRef = WeakReference(activity)
    }

}