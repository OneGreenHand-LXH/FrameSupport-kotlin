package com.ogh.frame.config

import android.text.TextUtils
import com.blankj.utilcode.util.SPStaticUtils

/**
 * 应用程序配置类: 用于保存用户相关信息及设置
 */
object BaseConfig {

    //true打开调试，false关闭调试
    const val DEBUG = true

    //通用请求地址
    fun getUrl(): String {
        return if (DEBUG) {
            "https://www.wanandroid.com/"
        } else {
            val urls = SPStaticUtils.getString("baseUrl")
            if (TextUtils.isEmpty(urls)) "https://www.wanandroid.com/" else urls
        }
    }

    fun setUrl(url: String) {
        if (!DEBUG)
            SPStaticUtils.put("baseUrl", url)
    }

    class ViewPage {
        companion object {
            const val START_INDEX = 1 //起始页下标
            const val PAGE_COUNT = 15 //每页的数据量
        }
    }
}