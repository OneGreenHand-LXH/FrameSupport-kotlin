package com.ogh.support.presenter

import com.ogh.frame.base.BaseModel
import com.ogh.frame.base.BasePresenter
import com.ogh.support.api.API
import com.ogh.support.bean.WenZhangBean
import com.ogh.support.view.activity.HeadFootExampleActivity

class HeadFootExamplePt(activity: HeadFootExampleActivity) : BasePresenter<HeadFootExampleActivity>(activity) {
    /**
     * 首页文章列表
     */
    fun getWenZhangList() {
        createRequestBuilder()
            .setLoadStyle(BaseModel.LoadStyle.DIALOG)
            .create()
            .get(API.GET_WEN_ZHANG + "/0/json", WenZhangBean::class.java)
    }
}