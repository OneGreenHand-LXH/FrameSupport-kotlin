package com.ogh.support.util

import android.text.TextUtils
import com.blankj.utilcode.util.SPStaticUtils

/**
 * @describe 用户操作工具类
 */
object UserUtil {
    /*************************登录和退出相关操作开始*******************************/
    /**
     * 获取token
     */
    fun getToken() = SPStaticUtils.getString("token")

    /**
     * 设置token
     */
    fun setToken(token: String) = SPStaticUtils.put("token", token)

    /**
     * 判断用户是否登录
     * 目前是根据token来判断
     */
    fun isLogin() = !TextUtils.isEmpty(getToken())

    /**
     * 退出登录
     */
    fun logout() {
        setToken("")
    }
    /*************************登录和退出相关操作结束 */
}