package com.ogh.support.view.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.ogh.frame.base.BaseQuickHolder
import com.ogh.support.R
import com.ogh.support.bean.WenZhangBean


class ExampleAdapter : BaseQuickAdapter<WenZhangBean.DataBean.DatasBean, BaseQuickHolder>(R.layout.item_common), LoadMoreModule {

    override fun convert(holder: BaseQuickHolder, item: WenZhangBean.DataBean.DatasBean) {
        holder.setText(R.id.item_content, item.title)
    }
}