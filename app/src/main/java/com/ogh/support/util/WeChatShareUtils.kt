package com.ogh.support.util

import android.graphics.Bitmap
import com.blankj.utilcode.util.ImageUtils
import com.ogh.frame.FrameApplication.Companion.getContext
import com.ogh.frame.util.ToastUtil
import com.ogh.support.R
import com.tencent.mm.opensdk.modelmsg.*
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import java.io.ByteArrayOutputStream

/**
 * 微信分享工具类
 */
object WeChatShareUtils {
    /**
     * 分享微信文本
     *
     * @param type 0：分享到好友  1：分享到朋友圈
     */
    fun shareWeChatTxt(content: String, type: Int) {
        val textObject = WXTextObject()
        textObject.text = content
        val mediaMessage = WXMediaMessage()
        mediaMessage.mediaObject = textObject
        mediaMessage.description = content
        val req: SendMessageToWX.Req = SendMessageToWX.Req()
        req.transaction = ""
        req.message = mediaMessage
        req.scene = type
        val appId: String = getContext().resources.getString(R.string.wx_appid)
        val iwxapi: IWXAPI = WXAPIFactory.createWXAPI(getContext(), appId)
        if (!iwxapi.isWXAppInstalled) {
            ToastUtil.showShortToast("未安装微信,请安装后再尝试分享")
            return
        }
        iwxapi.registerApp(appId)
        iwxapi.sendReq(req)
    }

    /**
     * 发起app网页分享
     *
     * @param type 0：分享到好友  1：分享到朋友圈
     */
    fun shareWeChatUrl(title: String, desc: String, url: String, type: Int) {
        //初始化一个WXWebpageObject，填写url
        val result = WXWebpageObject()
        result.webpageUrl = url
        //用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
        val mediaMessage = WXMediaMessage()
        mediaMessage.mediaObject = result
        mediaMessage.title = title //网页标题
        mediaMessage.description = desc //网页描述
        val bitmap: Bitmap = ImageUtils.getBitmap(R.mipmap.ic_launcher) //图片
        mediaMessage.thumbData = ImageUtils.bitmap2Bytes(bitmap)
        //构造一个Req
        val req: SendMessageToWX.Req = SendMessageToWX.Req()
        req.transaction = ""
        req.message = mediaMessage
        req.scene = type
        val appId: String = getContext().resources.getString(R.string.wx_appid)
        val iwxapi: IWXAPI = WXAPIFactory.createWXAPI(getContext(), appId)
        if (!iwxapi.isWXAppInstalled) {
            ToastUtil.showShortToast("未安装微信,请安装后再尝试分享")
            return
        }
        iwxapi.registerApp(appId)
        iwxapi.sendReq(req)
    }

    /**
     * 发起图片分享
     *
     * @param type 0：分享到好友  1：分享到朋友圈
     */
    fun shareWeChatImg(bitmap: Bitmap, type: Int) {
        val imgObj = WXImageObject(bitmap)
        val msg = WXMediaMessage()
        msg.mediaObject = imgObj
        val thumbBmp: Bitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true)
        bitmap.recycle()
        msg.thumbData = bmpToByteArray(thumbBmp) //设置缩略图
        val req: SendMessageToWX.Req = SendMessageToWX.Req()
        req.transaction = buildTransaction("img")
        req.message = msg
        req.scene = type
        val appId: String = getContext().resources.getString(R.string.wx_appid)
        val iwxapi: IWXAPI = WXAPIFactory.createWXAPI(getContext(), appId)
        if (!iwxapi.isWXAppInstalled) {
            ToastUtil.showShortToast("未安装微信,请安装后再尝试分享")
            return
        }
        iwxapi.registerApp(appId)
        iwxapi.sendReq(req)
    }

    private fun bmpToByteArray(bm: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()
    }

    private fun buildTransaction(type: String?): String {
        return if (type == null) System.currentTimeMillis().toString() else type + System.currentTimeMillis()
    }
}