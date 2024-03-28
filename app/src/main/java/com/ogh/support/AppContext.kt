package com.ogh.support

import android.content.Context
import androidx.multidex.MultiDex
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.ogh.frame.FrameApplication
import com.ogh.frame.config.BaseConfig

class AppContext : FrameApplication() {

    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        LogUtils.getConfig().isLogSwitch = BaseConfig.DEBUG //设置log开关
    }

    /**
     * 不改变onCreate()方法中的业务逻辑
     */
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}