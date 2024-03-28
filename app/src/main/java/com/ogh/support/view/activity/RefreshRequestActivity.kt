package com.ogh.support.view.activity

import android.os.Bundle
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ogh.frame.base.BaseQuickHolder
import com.ogh.frame.base.activity.BaseSwipeListActivity
import com.ogh.frame.bean.BaseBean
import com.ogh.frame.util.OnItemClickListener2
import com.ogh.frame.util.ToastUtil
import com.ogh.support.bean.WenZhangBean
import com.ogh.support.databinding.LayoutHeadFootExampleBinding
import com.ogh.support.presenter.RefreshRequestPt
import com.ogh.support.view.adapter.ExampleAdapter

/**
 * 上拉刷新和下拉加载
 */
class RefreshRequestActivity : BaseSwipeListActivity<LayoutHeadFootExampleBinding, RefreshRequestPt, BaseBean, WenZhangBean.DataBean.DatasBean>() {

    override fun setAdapter(): BaseQuickAdapter<WenZhangBean.DataBean.DatasBean, BaseQuickHolder> {
        return ExampleAdapter()
    }

    override fun userAdapterEmpty(): Boolean {
        return true
    }

    override fun loadMoreListRequest(page: Int) {
        mPresenter.getWenZhangList(page)
    }

    override fun onRefreshRequest() {
        mPresenter.getWenZhangList(0)
    }

    override fun setPresenter(): RefreshRequestPt {
        return RefreshRequestPt(this)
    }

    override fun init(savedInstanceState: Bundle?) {
        viewBinding.titlebar.setTitle("下拉刷新上拉加载示例")
        mPresenter.getWenZhangList(0)
        mBaseAdapter.setOnItemClickListener(object : OnItemClickListener2() {
            override fun onSingleClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                val item = adapter.getItem(position) as WenZhangBean.DataBean.DatasBean
                ToastUtil.showShortToast(item.title)
            }
        })
    }

    override fun requestSuccess(data: BaseBean, tag: Any, pageIndex: Int, pageCount: Int) {
        val wenZhangBean: WenZhangBean = data as WenZhangBean
        notifyAdapterStatus(wenZhangBean.data.datas, pageIndex+1, pageCount)//因为这个接口第一页从0开始的,不改变框架原本的逻辑,页数手动+1
    }
}