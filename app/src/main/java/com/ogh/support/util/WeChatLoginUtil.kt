package com.ogh.support.util

import android.content.Context
import com.ogh.frame.util.ToastUtil
import com.ogh.support.R
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory

/**
 * 微信登录工具类
 */
object WeChatLoginUtil {

    fun weChatLogin(context: Context) {
        val appId = context.resources.getString(R.string.wx_appid)
        val iwxapi: IWXAPI = WXAPIFactory.createWXAPI(context, appId)
        if (!iwxapi.isWXAppInstalled) {
            ToastUtil.showShortToast("未安装微信,请安装后再尝试登录")
            return
        }
        iwxapi.registerApp(appId)
        val req: SendAuth.Req = SendAuth.Req()
        req.scope = "snsapi_userinfo"
        req.state = "wechat_sdk_demo_test"
        iwxapi.sendReq(req)
    }
}