package com.ogh.support.presenter

import com.ogh.frame.base.BaseModel
import com.ogh.frame.base.BasePresenter
import com.ogh.support.api.API
import com.ogh.support.bean.WenZhangBean
import com.ogh.support.view.activity.RefreshRequestActivity

class RefreshRequestPt(activity: RefreshRequestActivity) : BasePresenter<RefreshRequestActivity>(activity) {
    /**
     * 首页文章列表
     */
    fun getWenZhangList(page: Int) {
        createRequestBuilder()
            .setLoadStyle(if (page == 0) BaseModel.LoadStyle.DIALOG else BaseModel.LoadStyle.NONE)
            .setPageIndex(page)
            .setPageCount(10)//改为每页10条，发现有时候没那么多数据,导致无法分页
            .create()
            .get(API.GET_WEN_ZHANG + "/$page/json?page_size=10", WenZhangBean::class.java)
    }
}