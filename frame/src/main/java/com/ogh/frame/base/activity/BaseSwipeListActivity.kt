package com.ogh.frame.base.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.chad.library.adapter.base.module.BaseLoadMoreModule
import com.ogh.frame.R
import com.ogh.frame.base.BasePresenter
import com.ogh.frame.base.BaseQuickHolder
import com.ogh.frame.base.BaseSwipeListView
import com.ogh.frame.bean.BaseBean
import com.ogh.frame.config.BaseConfig

/**
 * 自带recyclerview的基类
 * Tips:布局中的recyclerView的id必须是frame_recycleView
 */
abstract class BaseSwipeListActivity<T : ViewBinding,P: BasePresenter<*>,B : BaseBean,AB> :BaseSwipeActivity<T,P,B>(),BaseSwipeListView<B>{

    var mRecyclerView: RecyclerView? = null
    lateinit var mBaseAdapter: BaseQuickAdapter<AB, BaseQuickHolder>
    private var page = BaseConfig.ViewPage.START_INDEX

    override fun initCommon() {
        super.initCommon()
        mRecyclerView = findViewById(R.id.frame_recycleView)
        if (mRecyclerView == null) throw RuntimeException("布局中必须有RecyclerView,并且RecyclerView中的ID为frame_recycleView")
        mRecyclerView!!.layoutManager = setLayoutManager()
        mBaseAdapter = setAdapter()
//      getLoadMoreModule().loadMoreView = CustomLoadMoreView()
        getAdapter()
    }

    /**
     * 空布局时,Rv头部是否显示,结合[userAdapterEmpty()]使用
     */
    open fun isHeaderAndEmpty(): Boolean {
        return false
    }

    /**
     * 空布局时,Rv脚部是否显示,结合[UserAdapterEmpty()]使用
     */
    open fun isFooterAndEmpty(): Boolean {
        return false
    }

    /**
     * 无数据时,是否使用Adapter设置空布局(不能和frame_root_view一起使用)
     */
    open fun userAdapterEmpty(): Boolean {
        return false
    }

    private val loadMoreListener = OnLoadMoreListener { loadMoreListRequest(page) }

    //自动更新adapter状态
    fun notifyAdapterStatus(data: List<AB>?, pageIndex: Int, pageCount: Int) {
        val dataSize = data?.size ?: 0
        if (pageIndex == BaseConfig.ViewPage.START_INDEX) {
            page = pageIndex
            if (data.isNullOrEmpty()) {
                if (userAdapterEmpty()) mBaseAdapter.setList(null)
            } else {
                if (dataSize == pageCount) {
                    page++
                    getLoadMoreModule().setOnLoadMoreListener(loadMoreListener)
                } else getLoadMoreModule().setOnLoadMoreListener(null)
                mBaseAdapter.setList(data)
            }
        } else {
            if (data.isNullOrEmpty()) {
                getLoadMoreModule().loadMoreEnd(false)
            } else {
                page++
                mBaseAdapter.addData(data)
                if (dataSize == pageCount)
                    getLoadMoreModule().loadMoreComplete()
                else
                    getLoadMoreModule().loadMoreEnd(false)
            }
        }
    }

    private fun getLoadMoreModule(): BaseLoadMoreModule {
        return mBaseAdapter.loadMoreModule
    }

    override fun onRefresh() {
        super.onRefresh()
        getLoadMoreModule().setOnLoadMoreListener(null)
    }

    override fun loadMoreFailView() {
        getLoadMoreModule().loadMoreFail()
    }

   open fun setLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(this)
    }

    abstract fun setAdapter(): BaseQuickAdapter<AB, BaseQuickHolder>

    abstract fun loadMoreListRequest(page: Int)

    /**
     * 设置适配器,若UserAdapterEmpty()为true,切换布局只需setList(null)
     */
    private fun getAdapter() {
        if (userAdapterEmpty()) {
            val mEmptyView = LayoutInflater.from(mContext).inflate(getEmptyView(), mRecyclerView!!.parent as ViewGroup, false)
            mEmptyView.findViewById<TextView>(R.id.tv_view_pager_no_data_content).text = getEmptyViewMsg()
            mBaseAdapter.headerWithEmptyEnable = isHeaderAndEmpty()
            mBaseAdapter.footerWithEmptyEnable = isFooterAndEmpty()
            mBaseAdapter.setEmptyView(mEmptyView)
        }
        mRecyclerView!!.adapter = mBaseAdapter
    }

    /**
     * 手动切换适配器
     * 请在notifyAdapterStatus(data,loadMode,pageCount)之前调用
     */
    open fun changeAdapter(adapter: BaseQuickAdapter<AB, BaseQuickHolder>) {
        mBaseAdapter = adapter
        getAdapter()
    }

}