package com.ogh.frame.base


/**
 * @dessribe 通用P层
 */
abstract class BasePresenter<V : BaseRequestView<*>>() {
    private lateinit var mV: V

    constructor(v: V) : this() {
        mV = v
    }

    fun createRequestBuilder(): BaseModel.Builder {
        return BaseModel.Builder(mV)
    }

}