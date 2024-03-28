package com.ogh.support.view.activity

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.ogh.frame.base.activity.BaseSwipeActivity
import com.ogh.frame.bean.BaseBean
import com.ogh.support.bean.WenZhangBean
import com.ogh.support.databinding.LayoutHeadFootExampleBinding
import com.ogh.support.presenter.HeadFootExamplePt
import com.ogh.support.view.adapter.ExampleAdapter

/**
 * 添加头部和脚部的示例(带下拉刷新上拉加载)
 */
class HeadFootExampleActivity : BaseSwipeActivity<LayoutHeadFootExampleBinding, HeadFootExamplePt, BaseBean>() {
    private lateinit var adapter: ExampleAdapter

    override fun onRefreshRequest() {
        mPresenter.getWenZhangList()
    }

    override fun setPresenter(): HeadFootExamplePt {
        return HeadFootExamplePt(this)
    }

    override fun init(savedInstanceState: Bundle?) {
        viewBinding.titlebar.setTitle("添加头部和脚部的示例")
        viewBinding.frameRecycleView.layoutManager = LinearLayoutManager(mContext)
        adapter = ExampleAdapter()
        //adapter.headerWithEmptyEnable=true;//空布局时,头部显示出来
        //adapter.footerWithEmptyEnable=true;//空布局时,脚部显示出来
        initHeadFootView()
        viewBinding.frameRecycleView.adapter=adapter
        adapter.setEmptyView(getEmptyView()) //设置空布局(引用资源文件必须放在setAdapter()后才有效)
        mPresenter.getWenZhangList()
    }

    /**
     * 初始化头部和脚部
     */
    private fun initHeadFootView() {
        val head = TextView(mContext)
        head.text = "我是adapter的头部"
        head.textSize = 18f
        head.setTextColor(Color.parseColor("#3BC68C"))
        head.gravity = Gravity.CENTER
        val foot = TextView(mContext)
        foot.text = "我是adapter的脚部"
        foot.textSize = 18f
        foot.setTextColor(Color.parseColor("#3BC68C"))
        foot.gravity = Gravity.CENTER
        adapter.addHeaderView(head)
        adapter.addFooterView(foot)
    }

    override fun requestSuccess(data: BaseBean, tag: Any, pageIndex: Int, pageCount: Int) {
        val wenZhangBean = data as WenZhangBean
        adapter.setList(wenZhangBean.data.datas)
    }
}