package com.ogh.support.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

/**
 * 跳转工具类
 */
object IntentUtil {

    fun goActivity(context: Context, activity: Class<*>) {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.setClass(context, activity)
        context.startActivity(intent)
    }

    fun goActivity(context: Context, activity: Class<*>, ifLogin: Boolean) {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//      if (ifLogin && !UserUtil.isLogin()) {
//      ToastUtil.showShortToast("请先登录");
//      intent.setClass(context, LoginActivity::class.java);
//      } else
        intent.setClass(context, activity)
        context.startActivity(intent)
    }

    /**
     * 跳转activity,通过bundle方式传入数据
     */
    fun goActivity(context: Context, activity: Class<*>, bundle: Bundle?, ifLogin: Boolean) {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK //在Activity上下文之外启动Activity
//      if (ifLogin && !UserUtil.isLogin()) {
//      ToastUtil.showShortToast("请先登录");
//      intent.setClass(context, LoginActivity::class.java);
//      } else
        intent.setClass(context, activity)
        if (bundle != null) intent.putExtras(bundle)
        context.startActivity(intent)
    }

    /**
     * 跳转activity并回调,通过bundle方式传入数据
     */
    fun goActivityForResult(context: Activity, activity: Class<*>?, bundle: Bundle?, requestCode: Int, ifLogin: Boolean) {
        val intent = Intent()
//      if (ifLogin && !UserUtil.isLogin()) {
//      ToastUtil.showShortToast("请先登录");
//      intent.setClass(context, LoginActivity::class.java);
//      } else
        intent.setClass(context, activity!!)
        if (bundle != null) intent.putExtras(bundle)
        context.startActivityForResult(intent, requestCode)
    }

    /**
     * 跳转activity,通过map方式传入数据,主要配合CommonUtil中goLocationActivity方法使用
     * 携带的数据(目前只支持 String、Boolean、Integer、Double、Long、Float、Bundle、Parcelable、Serializable)
     */
    fun goActivity(context: Context, activity: Class<*>?, param: Map<String, Any>?, ifLogin: Boolean) {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//      if (ifLogin && !UserUtil.isLogin()) {
//      ToastUtil.showShortToast("请先登录");
//      intent.setClass(context, LoginActivity::class.java);
//      } else
        intent.setClass(context, activity!!)
        putIntent(param, intent)
        context.startActivity(intent)
    }

    /**
     * 跳转activity并回调,通过map方式传入数据,主要配合CommonUtil中goLocationActivity方法使用
     * 携带的数据(目前只支持 String、Boolean、Integer、Double、Long、Float、Bundle、Parcelable、Serializable)
     */
    fun goActivityForResult(context: Activity, activity: Class<*>?, param: Map<String, Any>?, requestCode: Int, ifLogin: Boolean) {
        val intent = Intent()
//      if (ifLogin && !UserUtil.isLogin()) {
//      ToastUtil.showShortToast("请先登录");
//      intent.setClass(context, LoginActivity::class.java);
//      } else
        intent.setClass(context, activity!!)
        putIntent(param, intent)
        context.startActivityForResult(intent, requestCode)
    }

    private fun putIntent(param: Map<String, Any>?, intent: Intent) {

        if (param != null && param.isNotEmpty()) {
            for ((key, value) in param) {
                when (value) {
                    is String -> {
                        intent.putExtra(key, value)
                    }
                    is Boolean -> {
                        intent.putExtra(key, value)
                    }
                    is Int -> {
                        intent.putExtra(key, value)
                    }
                    is Double -> {
                        intent.putExtra(key, value)
                    }
                    is Long -> {
                        intent.putExtra(key, value)
                    }
                    is Float -> {
                        intent.putExtra(key, value)
                    }
                    is Bundle -> {
                        intent.putExtra(key, value)
                    }
                    is Parcelable -> {
                        intent.putExtra(key, value)
                    }
                    is Serializable -> {
                        intent.putExtra(key, value)
                    }
                }
            }
        }
    }
}