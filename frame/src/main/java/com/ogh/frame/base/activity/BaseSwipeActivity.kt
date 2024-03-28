package com.ogh.frame.base.activity

//noinspection SuspiciousImport
import android.R
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.ogh.frame.base.BasePresenter
import com.ogh.frame.base.BaseSwipeView
import com.ogh.frame.bean.BaseBean
import com.ogh.frame.widget.VpSwipeRefreshLayout

/**
 *  带下拉刷新的Activity
 */
abstract class BaseSwipeActivity<T : ViewBinding, P : BasePresenter<*>, B : BaseBean> : BaseRequestActivity<T, P, B>(), SwipeRefreshLayout.OnRefreshListener, BaseSwipeView<B> {

    lateinit var mSwipeRefreshLayout: VpSwipeRefreshLayout

    override fun setContentView(view: View) {
        mSwipeRefreshLayout = VpSwipeRefreshLayout(this)
        mSwipeRefreshLayout.setColorSchemeResources(R.color.holo_orange_light, R.color.holo_blue_bright, R.color.holo_green_light, R.color.holo_red_light)
        mSwipeRefreshLayout.addView(view)
        mSwipeRefreshLayout.layoutParams=ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mSwipeRefreshLayout.setOnRefreshListener(this)
        super.setContentView(mSwipeRefreshLayout)
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