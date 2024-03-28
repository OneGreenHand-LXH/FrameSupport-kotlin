package com.ogh.support.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.ogh.support.R
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory

/**
 * 微信分享回调
 */
class WXEntryActivity : Activity(), IWXAPIEventHandler {

    private var iwxapi: IWXAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        iwxapi = WXAPIFactory.createWXAPI(this, this.resources.getString(R.string.wx_appid))
        val handleIntent = iwxapi?.handleIntent(intent, this)
        //下面代码是判断微信分享后返回WXEnteryActivity的，如果handleIntent==false,说明没有调用IWXAPIEventHandler，则需要在这里销毁这个透明的Activity;
        //下面代码是判断微信分享后返回WXEnteryActivity的，如果handleIntent==false,说明没有调用IWXAPIEventHandler，则需要在这里销毁这个透明的Activity;
        if (!handleIntent!!) finish()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        iwxapi!!.handleIntent(getIntent(), this)
    }

    override fun onReq(p0: BaseReq?) {
        finish()
    }

    override fun onResp(baseResp: BaseResp?) {
//        val result = ""
//        if (baseResp?.type == ConstantsAPI.COMMAND_SENDAUTH) {//登录
//           var resp =  baseResp as SendAuth.Resp
//            resp.code
//            resp.errCode
//        }
        finish()
    }
}