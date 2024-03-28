package com.ogh.frame.base.activity

import android.view.View
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.LogUtils
import com.ogh.frame.R
import com.ogh.frame.base.BasePresenter
import com.ogh.frame.base.BaseRequestView
import com.ogh.frame.bean.BaseBean
import com.ogh.frame.loadingView.VaryViewHelperController
import com.ogh.frame.util.CustomClickListener
import com.ogh.frame.util.ToastUtil

/**
 * 带网络请求的Activity
 */
abstract class BaseRequestActivity<T : ViewBinding, P : BasePresenter<*>, B : BaseBean> : BaseActivity<T>(), BaseRequestView<B> {

    lateinit var mPresenter: P
    private var mVaryViewHelperController: VaryViewHelperController? = null

    override fun initCommon() {
        super.initCommon()
        val frameRootView: View? = findViewById(R.id.frame_root_view)
        if (null != frameRootView)
            mVaryViewHelperController = VaryViewHelperController(frameRootView, getEmptyView())
        mPresenter = setPresenter()
    }

    abstract fun setPresenter(): P

    override fun requestFail(data: BaseBean, tag: Any) {
        ToastUtil.showShortToast(data.errorMsg)
    }

    open fun reRequest() {}

    override fun requestError(e: Throwable, tag: Any) {
        LogUtils.e("okhttp", e.message)
    }

    override fun showLoadingView() {
        mVaryViewHelperController?.showLoading()
    }

    override fun showEmptyView() {
        mVaryViewHelperController?.showEmpty(getEmptyViewMsg())
    }

    override fun showNetErrorView(tips: String) {
        mVaryViewHelperController?.showNetworkError(object : CustomClickListener() {
            override fun onSingleClick(v: View) {
                reRequest()
            }
        }, tips)
    }

    override fun refreshView() {
        mVaryViewHelperController?.restore()
    }

    override fun tokenOverdue() {   //登录过期
    }

    //设置空数据提示文本
    open fun getEmptyViewMsg(): String {
        return getResString(R.string.frame_no_data)
    }

    //设置空数据布局(重写即为替换)
    open fun getEmptyView(): Int {
        return R.layout.frame_layout_no_data
    }
}