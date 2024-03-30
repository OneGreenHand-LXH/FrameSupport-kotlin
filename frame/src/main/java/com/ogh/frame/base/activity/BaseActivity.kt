package com.ogh.frame.base.activity

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.BusUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.ogh.frame.R
import com.gyf.immersionbar.ImmersionBar
import com.ogh.frame.base.BaseView
import com.ogh.frame.util.NotificationControlManager
import com.ogh.frame.view.LoadingDialog
import java.lang.reflect.ParameterizedType

/**
 * Activity基类，所有的Activity均继承它
 */
abstract class BaseActivity<T : ViewBinding> : AppCompatActivity(), BaseView {
    val mContext: Activity = this
    lateinit var viewBinding: T
    var mLoadingDialog: LoadingDialog? = null
    private var mIsDestroyed = false //是否真的被finish

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val type = this.javaClass.genericSuperclass
        if (type is ParameterizedType) {
            try {
                val clazz = type.actualTypeArguments[0] as Class<*>
                val method = clazz.getMethod("inflate", LayoutInflater::class.java)
                viewBinding = method.invoke(null, layoutInflater) as T
                setContentView(viewBinding.root)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        initCommon()
        init(savedInstanceState) //初始化
        if (isImmersionBarEnabled()) //初始化沉浸式状态栏,所有子类都将继承这些相同的属性,请在设置界面之后设置
            initImmersionBar()
        if (isRegisterBus()) BusUtils.register(this)
    }

    protected open fun initCommon() {}

    abstract fun init(savedInstanceState: Bundle?)

    open fun getResString(res: Int): String {
        return resources.getString(res)
    }

    /**
     * 是否需要注册BusUtils
     */
    open fun isRegisterBus(): Boolean {
        return false
    }

    /**
     * 是否需要开启沉浸式
     */
    open fun isImmersionBarEnabled(): Boolean {
        return true
    }

    /**
     * 默认设置状态栏配置(状态栏为主题色、字体自动变色(须指定状态栏颜色),同时解决状态栏和布局重叠问题)
     */
    private fun initImmersionBar() {
        initImmersionBar(R.color.frame_white)
    }

    /**
     * 设置其他颜色,主要用于和通用标题栏颜色不符的情况
     */
    open fun initImmersionBar(color: Int) {
        ImmersionBar.with(this).statusBarColor(color).autoStatusBarDarkModeEnable(true, 0.2f)
            .fitsSystemWindows(true).init()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideKeyboard(v, ev)) KeyboardUtils.hideSoftInput(this)
        }
        return super.dispatchTouchEvent(ev)
    }

    // Return whether touch the view.
    private fun isShouldHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            dismissLoadingDialog()
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 显示加载对话框
     */
    override fun showLoadingDialog(msg: String, isCancel: Boolean) {
        val message = if (TextUtils.isEmpty(msg)) getResString(R.string.frame_load_ing) else msg
        if (mLoadingDialog == null) mLoadingDialog = LoadingDialog(mContext)
        mLoadingDialog!!.setCancel(isCancel)
        mLoadingDialog!!.setMsg(message)
        if (!mLoadingDialog!!.isShowing) mLoadingDialog!!.show()
    }

    /**
     * 隐藏加载对话框
     */
    override fun dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog!!.isShowing) mLoadingDialog!!.dismiss()
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) destroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroy()
    }

    /**
     * 解决activity被finish后onDestroy()不立即执行问题
     */
    private fun destroy() {
        if (!isDestroyed) { // 回收资源
            mIsDestroyed = true
            dismissLoadingDialog()
            if (isRegisterBus()) BusUtils.unregister(this)
            NotificationControlManager.getInstance()?.dismissDialog()
        }
    }
}