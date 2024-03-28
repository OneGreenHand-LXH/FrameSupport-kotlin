package com.ogh.frame.util

import android.graphics.Color
import android.text.TextUtils
import com.blankj.utilcode.util.ToastUtils

/**
 * 吐司工具类(便于后期更换)
 */
object ToastUtil {

   fun showShortToast(content: String?) {
       if (!TextUtils.isEmpty(content))
           ToastUtils.make().setTextColor(Color.WHITE).setBgColor(Color.BLACK).show(content)
   }

   fun showLongToast(content: String?) {
       if (!TextUtils.isEmpty(content))
           ToastUtils.make().setTextColor(Color.WHITE).setDurationIsLong(true).setBgColor(Color.BLACK).show(content)
   }
}