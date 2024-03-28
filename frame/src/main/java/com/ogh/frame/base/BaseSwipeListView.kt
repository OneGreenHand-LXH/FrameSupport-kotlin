package com.ogh.frame.base

import com.ogh.frame.bean.BaseBean

interface BaseSwipeListView<B : BaseBean> : BaseSwipeView<B> {
    fun loadMoreFailView()
}