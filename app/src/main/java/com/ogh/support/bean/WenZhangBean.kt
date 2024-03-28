package com.ogh.support.bean

import com.ogh.frame.bean.BaseBean


/**
 * @describe 文章实体类
 */
data class WenZhangBean(val data: DataBean) : BaseBean() {

    data class DataBean(val datas: List<DatasBean>) {
        data class DatasBean(val title: String)
    }

    override fun isEmpty(): Boolean {
        return data.datas.isEmpty()
    }
}