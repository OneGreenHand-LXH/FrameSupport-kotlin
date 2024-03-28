package com.ogh.frame.base

import com.ogh.frame.bean.BaseBean

interface BaseSwipeView<B : BaseBean> : BaseRequestView<B> {
    fun resetRefreshView()
}