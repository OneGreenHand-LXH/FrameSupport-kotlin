package com.ogh.frame.loadingView

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.ogh.frame.R
import com.ogh.frame.util.CustomClickListener

class VaryViewHelperController {

    private lateinit var helper: IVaryViewHelper
    private var emptyView = R.layout.frame_layout_no_data

    constructor(helper: IVaryViewHelper) : super() {
        this.helper = helper
    }

    constructor(view: View, emptyView: Int) : super() {
        helper = VaryViewHelper(view)
        this.emptyView = emptyView
    }

    fun showNetworkError(onClickListener: CustomClickListener, tips: String) {
        val layout = helper.inflate(R.layout.frame_layout_error)
        if (TextUtils.isEmpty(tips)) {
            (layout.findViewById<View>(R.id.v_tips) as TextView).text = R.string.frame_view_net_error.toString()
        } else
            (layout.findViewById<View>(R.id.v_tips) as TextView).text = tips
        val againBtn = layout.findViewById<TextView>(R.id.tv_error_load)
        againBtn.setOnClickListener(onClickListener)
        helper.showLayout(layout)
    }

    fun showEmpty(emptyMsg: String) {
        val layout = helper.inflate(emptyView)
        val textView = layout.findViewById<TextView>(R.id.tv_view_pager_no_data_content)
        if (!TextUtils.isEmpty(emptyMsg))
            textView.text = emptyMsg
        helper.showLayout(layout)
    }

    fun showLoading() {
        val layout = helper.inflate(R.layout.frame_layout_loading)
        helper.showLayout(layout)
    }

    fun restore() {
        helper.restoreView()
    }

}