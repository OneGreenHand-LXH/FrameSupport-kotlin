package com.ogh.frame

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.ogh.frame.util.AppLifecycleCallback

/**
 * description: 基础框架Application
 */
open class FrameApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var mContext: Context
        fun getContext() = mContext
    }

    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
        registerActivityLifecycleCallbacks(AppLifecycleCallback())//注册Activity生命周期
    }

}