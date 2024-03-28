package com.ogh.frame.base.fragment

//noinspection SuspiciousImport
import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import androidx.viewbinding.ViewBinding
import com.ogh.frame.base.BasePresenter
import com.ogh.frame.base.BaseSwipeView
import com.ogh.frame.bean.BaseBean
import com.ogh.frame.widget.VpSwipeRefreshLayout

/**
 * 带下拉刷新的fragment
 */
abstract class BaseSwipeFragment<T : ViewBinding, P : BasePresenter<*>, B : BaseBean> : BaseRequestFragment<T, P, B>(), OnRefreshListener, BaseSwipeView<B> {
   lateinit var mSwipeRefreshLayout: VpSwipeRefreshLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)
        mSwipeRefreshLayout = VpSwipeRefreshLayout(mActivity)
        mSwipeRefreshLayout.setColorSchemeResources(R.color.holo_orange_light, R.color.holo_blue_bright, R.color.holo_green_light, R.color.holo_red_light)
        mSwipeRefreshLayout.addView(rootView)
        mSwipeRefreshLayout.layoutParams=ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mSwipeRefreshLayout.setOnRefreshListener(this)
        return mSwipeRefreshLayout
    }

    override fun onRefresh() {
        onRefreshRequest()
    }

    abstract fun onRefreshRequest()

    override fun resetRefreshView() {
        if (mSwipeRefreshLayout.isRefreshing)
            mSwipeRefreshLayout.isRefreshing = false
    }
}