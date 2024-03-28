package com.ogh.frame

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

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
    }

}