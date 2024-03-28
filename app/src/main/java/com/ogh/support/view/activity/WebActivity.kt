package com.ogh.support.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.WindowManager
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.ogh.frame.base.activity.BaseActivity
import com.ogh.support.R
import com.ogh.support.databinding.ActivityWebBinding

/**
 * 可以播放视频的WebView
 */
class WebActivity : BaseActivity<ActivityWebBinding>() {

    companion object{
        fun openActivity(context: Context) {
            context.startActivity(Intent(context, WebActivity::class.java))
        }

        fun openActivity(context: Context, isHorizontalScreen: Boolean) {
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra("isHorizontalScreen", isHorizontalScreen)
            context.startActivity(intent)
        }
    }

    override fun isImmersionBarEnabled(): Boolean {
        return false
    }

    override fun init(savedInstanceState: Bundle?) {
        window.setFormat(PixelFormat.TRANSLUCENT) //为了避免视频闪屏和透明问题
        val isHorizontalScreen: Boolean = intent.getBooleanExtra("isHorizontalScreen", false)
        viewBinding.webView.setProgressBar(viewBinding.pbWebBase)
        if (isHorizontalScreen) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE //横屏可翻转
            ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init()
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //保持屏幕常亮
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED//竖屏
            ImmersionBar.with(this).statusBarColor(R.color.c_FFFFFF).autoStatusBarDarkModeEnable(true, 0.2f).fitsSystemWindows(true).init()
        }
        viewBinding.webView.loadUrl("https://www.baidu.com")
        //viewBinding.webView.loadUrl("http://debugtbs.qq.com");//用来检测X5内核是否安装
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK)
            viewBinding.webView.doFileChoose(data)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (viewBinding.webView.canGoBack()) {
            viewBinding.webView.goBack()
        } else finish()
    }

    override fun onDestroy() {
        viewBinding.webView.destroy() //为了使WebView退出时音频或视频关闭
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        try {
            viewBinding.webView.onResume()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            viewBinding.webView.onPause()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}