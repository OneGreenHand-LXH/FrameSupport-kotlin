package com.ogh.support.view.activity

import android.os.Bundle
import com.ogh.frame.base.activity.BaseSwipeActivity
import com.ogh.frame.bean.BaseBean
import com.ogh.frame.util.ToastUtil
import com.ogh.support.bean.WenZhangBean
import com.ogh.support.databinding.LayoutHeadFootExampleBinding
import com.ogh.support.presenter.RequestErrorExamplePt

/**
 * 无数据显示示例
 */
class NoDataExampleActivity : BaseSwipeActivity<LayoutHeadFootExampleBinding, RequestErrorExamplePt, BaseBean>() {

    override fun onRefreshRequest() {
        ToastUtil.showShortToast("刷新了")
        mSwipeRefreshLayout.isRefreshing=false
    }

    override fun setPresenter(): RequestErrorExamplePt {
        return RequestErrorExamplePt(this)
    }

    override fun reRequest() {
        ToastUtil.showShortToast("重新请求")
    }

    override fun init(savedInstanceState: Bundle?) {
        viewBinding.titlebar.setTitle("无数据显示示例")
        mPresenter.getWenZhangList()
    }

    override fun getEmptyViewMsg(): String {
        return "为了看出效果,这里请求了第999页数据,所以返回的数据为空"
    }

    override fun requestSuccess(data: BaseBean, tag: Any, pageIndex: Int, pageCount: Int) {
        //请求完成后返回的数据是空的，在实体bean中isEmpty()为true，baseModel会自动处理,并切换为空布局显示
        val wenZhangBean: WenZhangBean = data as WenZhangBean
        ToastUtil.showShortToast(wenZhangBean.errorMsg)
    }
}