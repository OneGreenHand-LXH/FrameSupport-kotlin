package com.ogh.frame.util

import android.app.Activity
import android.app.Application
import android.os.Bundle

class AppLifecycleCallback: Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) { //获取Activity弱引用
        ForegroundActivityManager.getInstance().setCurrentActivity(activity)
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) { //获取Activity弱引用
       // ForegroundActivityManager.getInstance().setCurrentActivity(activity)
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}