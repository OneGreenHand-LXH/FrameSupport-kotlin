package com.ogh.frame.base.fragment

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.ogh.frame.R
import com.ogh.frame.base.BaseView
import com.ogh.frame.view.LoadingDialog
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.ParameterizedType

/**
 * Fragment基类
 */
abstract class BaseFragment<T :ViewBinding> :Fragment(),BaseView {
    lateinit var mActivity: Activity
    lateinit var viewBinding: T
    var progressDialog: LoadingDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mActivity = requireActivity()
        val type = this.javaClass.genericSuperclass
        if (type is ParameterizedType) {
            try {
                val clazz = type.actualTypeArguments[0] as Class<*>
                val method = clazz.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.javaPrimitiveType)
                viewBinding = method.invoke(null, layoutInflater, container, false) as T
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCommon()
        if (isRegisterBus())
            EventBus.getDefault().register(this)
        init(savedInstanceState)
    }

    protected open fun initCommon() {}

    abstract fun init(savedInstanceState: Bundle?)

    open fun getResString(res: Int): String {
        return resources.getString(res)
    }

    /**
     * 是否需要注册EventBus
     */
    open fun isRegisterBus(): Boolean {
        return false
    }

    /**
     * 显示加载对话框
     */
    override fun showLoadingDialog(msg: String, isCancel: Boolean) {
        val message = if (TextUtils.isEmpty(msg)) getResString(R.string.frame_load_ing) else msg
        if (progressDialog == null) progressDialog = LoadingDialog(mActivity)
        progressDialog!!.setCancel(isCancel)
        progressDialog!!.setMsg(message)
        if (!progressDialog!!.isShowing) progressDialog!!.show()
    }

    /**
     * 隐藏加载对话框
     */
    override fun dismissLoadingDialog() {
        if (progressDialog != null && progressDialog!!.isShowing) progressDialog!!.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        //页面销毁时隐藏dialog，否则重新打开页面时可能会报java.lang.IllegalArgumentException: View not attached to window manager
        dismissLoadingDialog()
        if (isRegisterBus()) EventBus.getDefault().unregister(this)
    }
}