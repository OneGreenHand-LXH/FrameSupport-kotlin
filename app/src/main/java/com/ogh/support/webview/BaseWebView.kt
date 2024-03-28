package com.ogh.support.webview

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.util.Base64
import android.webkit.*
import android.widget.ProgressBar
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.ogh.frame.util.ToastUtil
import com.ogh.support.R
import com.ogh.support.util.ImageSaveUtil

/**
 * 基类 WebView
 * 备注: 使用的时候不要设置 android:scrollbars="none"，不然部分机型会显示空白
 */
class BaseWebView : WebView {
    private var mProgressBar: ProgressBar? = null
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    fun init(context: Context?) {
        val webSettings: WebSettings = settings
        webSettings.mediaPlaybackRequiresUserGesture=false // 允许自动播放多媒体
        webSettings.loadsImagesAutomatically=true //设置自动加载图片
        webSettings.javaScriptEnabled=true // 设置支持javascript脚本
        webSettings.javaScriptCanOpenWindowsAutomatically=true //设置允许js弹出alert对话框
        webSettings.useWideViewPort=true //设置webview推荐使用的窗口，使html界面自适应屏幕
        webSettings.loadWithOverviewMode=true // 缩放至屏幕的大小
        webSettings.domStorageEnabled=true //DOM存储API是否可用
        webSettings.databaseEnabled=true //数据库存储API是否可用
        webSettings.allowFileAccess=true //使用 File 协议
        webSettings.mixedContentMode=WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webSettings.cacheMode=WebSettings.LOAD_NO_CACHE //不缓存
        setDownloadListener(object : DownloadListener { //下载事件
            override fun onDownloadStart(s: String, s1: String, s2: String, s3: String, l: Long) {
                try {
                    if (s.startsWith("data:image")) { //下载图片
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) { //android 10以下
                            PermissionUtils.permission(PermissionConstants.STORAGE).callback(object : PermissionUtils.SimpleCallback {
                                    override fun onGranted() {
                                        saveImage(s)
                                    }

                                    override fun onDenied() {
                                        ToastUtil.showShortToast("没有权限")
                                    }
                                })
                                .request()
                        } else saveImage(s)
                    } else { //调用系统下载器
                        val intent = Intent()
                        intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.action="android.intent.action.VIEW"
                        intent.data=Uri.parse(s)
                        getContext().startActivity(intent)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
        webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: FileChooserParams): Boolean {
                this@BaseWebView.filePathCallback = filePathCallback
                if (getContext() is Activity) {
                    val i = Intent(Intent.ACTION_GET_CONTENT)
                    i.addCategory(Intent.CATEGORY_OPENABLE)
                    i.type="image/*"
                    (getContext() as Activity).startActivityForResult(Intent.createChooser(i, "File Browser"), 1)
                    return true
                }
                return false
            }

            override fun onProgressChanged(view: WebView, newProgress: Int) {
                if (mProgressBar != null) mProgressBar!!.progress = newProgress
                super.onProgressChanged(view, newProgress)
            }

            override fun onJsAlert(webView: WebView, url: String, message: String, jsResult: JsResult): Boolean {
                val b = AlertDialog.Builder(context)
                b.setTitle("提示")
                b.setMessage(message)
                b.setPositiveButton(resources.getString(R.string.sure)) { dialog, which -> jsResult.confirm() }
                b.setCancelable(false)
                b.create().show()
                return true
            }
        }
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
                /**
                 * 防止加载网页时调起系统浏览器
                 */
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    webView.loadUrl(url)
                    return false
                }
                try {  // Otherwise allow the OS to handle things like tel, mailto, etc.
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
                    getContext().startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return true
            }

            override fun onPageStarted(webView: WebView, url: String, favicon: Bitmap?) {
                if (mProgressBar != null) mProgressBar!!.visibility = VISIBLE
                super.onPageStarted(webView, url, favicon)
            }

            override fun onPageFinished(webView: WebView, url: String) {
                super.onPageFinished(webView, url)
                if (!webSettings.loadsImagesAutomatically) //不设置的话，部分机型不显示图片
                    webSettings.loadsImagesAutomatically=true
                if (mProgressBar != null) mProgressBar!!.visibility = GONE
            }
        }
        addJavascriptInterface(JSInterface(getContext()), "JSInterface")
    }

    /**
     * 保存图片到本地
     *
     * @param url 图片地址 data:image/png;base64格式
     */
    fun saveImage(url: String) {
        try {
            val bytes = Base64.decode(url.split(",".toRegex()).toTypedArray()[1], Base64.DEFAULT)
            val bitmap: Bitmap? = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            if (bitmap == null) {
                ToastUtil.showShortToast("下载失败,请稍后重试!")
                return
            }
            val uri: Uri? = ImageSaveUtil.saveAlbum(context, bitmap, Bitmap.CompressFormat.JPEG, 90, true)
            ToastUtil.showShortToast(if (uri == null) "下载失败,请稍后重试!" else "图片下载完成")
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtil.showShortToast("下载失败,请稍后重试!")
        }
    }

    fun doFileChoose(data: Intent?) {
        if (filePathCallback == null) return
        if (data == null) {
            filePathCallback!!.onReceiveValue(null)
            return
        }
        var results: Array<Uri>? = null
        val dataString: String? = data.dataString
        val clipData: ClipData? = data.clipData
        if (clipData != null) {
            results = Array(clipData.itemCount) { Uri.parse("") }//创建指定长度的数组,里面的值为空
            for (i in 0 until clipData.itemCount) {
                val item: ClipData.Item = clipData.getItemAt(i)
                results[i]=item.uri
            }
        }
        if (dataString != null)
            results = arrayOf(Uri.parse(dataString))
        filePathCallback!!.onReceiveValue(results)
        filePathCallback = null
    }

    fun setProgressBar(progressBar: ProgressBar?) {
        mProgressBar = progressBar
        mProgressBar?.max = 100 //设置加载进度最大值
    }
}