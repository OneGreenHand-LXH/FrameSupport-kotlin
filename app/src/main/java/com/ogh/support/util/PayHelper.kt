package com.ogh.support.util

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import com.alipay.sdk.app.PayTask
import com.ogh.frame.FrameApplication.Companion.getContext
import com.ogh.frame.util.ToastUtil
import com.ogh.support.bean.PayResult
import com.ogh.support.bean.WXResult
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory

/**
 * @describe 支付工具类
 */
class PayHelper private constructor() {

    companion object {
        private var mPayHelper: PayHelper? = null

        fun getInstance(): PayHelper {
            if (mPayHelper == null) {
                synchronized(this) {
                    if (mPayHelper == null)
                        mPayHelper = PayHelper()
                }
            }
            return mPayHelper!!
        }
    }

    /********************************微信支付**************************************/
    private var wxapi: IWXAPI? = null

    fun wexPay(result: WXResult?) {
        if (wxapi == null) {
            wxapi = WXAPIFactory.createWXAPI(getContext(), result?.appId)
            if (!wxapi!!.isWXAppInstalled) {
                ToastUtil.showShortToast("未安装微信客户端")
                return
            }
            wxapi!!.registerApp(result?.appId) // 将该app注册到微信
        }
        val req = PayReq()
        if (result != null) { //TODO  这里要根据后台返回的字段名和返回体，进行修改
            req.appId = result.appId
            req.partnerId = result.partnerId
            req.prepayId = result.prepayId
            req.nonceStr = result.nonceStr
            req.timeStamp = result.timeStamp
            req.packageValue = "Sign=WXPay"
            req.sign = result.sign
            wxapi!!.sendReq(req)
        }
    }

    /********************************支付宝支付**************************************/
    private val SDK_PAY_FLAG = 1

    fun aliPay(activity: Activity, orderInfo: String, mIPayListener: IPayListener?) {
        this.mIPayListener = mIPayListener
        val payRunnable = Runnable {
            val alipay = PayTask(activity)
            val result: Map<String, String> = alipay.payV2(orderInfo, true)
            val msg = Message()
            msg.what = SDK_PAY_FLAG
            msg.obj = result
            mHandler.sendMessage(msg)
        }
        // 必须异步调用
        val payThread = Thread(payRunnable)
        payThread.start()
    }

    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                SDK_PAY_FLAG -> {
                    val payResult = PayResult(msg.obj as Map<String, String>)
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    val resultInfo: String? = payResult.result // 同步返回需要验证的信息
                    val resultStatus: String? = payResult.resultStatus
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        if (mIPayListener != null) mIPayListener!!.onSuccess(resultInfo)
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        if (mIPayListener != null) mIPayListener!!.onFail(resultInfo)
                    }
                }
            }
        }
    }

    //支付宝支付结果回调
    private var mIPayListener: IPayListener? = null

    fun setIPayListener(mIPayListener: IPayListener) {
        this.mIPayListener = mIPayListener
    }

    interface IPayListener {
        fun onSuccess(resultInfo: String?)
        fun onFail(resultInfo: String?)
    }

}