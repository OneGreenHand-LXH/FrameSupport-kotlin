package com.ogh.support.api

import com.ogh.frame.config.BaseConfig

object API {
    private val BASE_URL = BaseConfig.getUrl() //接口统一请求地址
    var GET_WEN_ZHANG = BASE_URL + "article/list" //首页文章列表
}