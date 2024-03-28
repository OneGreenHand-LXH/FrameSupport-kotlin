package com.ogh.frame.base

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ogh.frame.util.CustomClickListener
import io.reactivex.rxjava3.disposables.Disposable

class BaseQuickHolder(view: View) : BaseViewHolder(view) {

    private var mDisposable: Disposable? = null

    /**
    * 设置倒计时
    */
    fun addDisposable(disposable: Disposable) {
        mDisposable = disposable
    }

    /**
     * 获取计时
     */
    fun getDisposable(): Disposable? {
        return mDisposable
    }

    /**
     * 销毁倒计时
     */
    fun disDisposable() {
        if (mDisposable != null && !mDisposable!!.isDisposed) {
            mDisposable!!.dispose()
            mDisposable = null
        }
    }

    /**
     * 获取图片控件
     */
    fun getImgView(viewId: Int): ImageView {
        return getView(viewId)
    }

    /**
     * 设置能不能点击
     */
    override fun setEnabled(viewId: Int, isEnabled: Boolean): BaseQuickHolder {
        val v = getView<View>(viewId)
        v.isEnabled = isEnabled
        return this
    }

    /**
     * 点击事件(已经做了暴力点击处理)
     */
    fun setOnClickListener(viewId: Int, listener: CustomClickListener): BaseQuickHolder{
        val view = getView<View>(viewId)
        view.setOnClickListener(listener)
        return this
    }

    /**
     * 设置布局管理器
     * direction （0 水平 1 垂直）
     */
    fun setLayoutManager(viewId: Int, adapter: RecyclerView.Adapter<*>, mContext: Context, direction: Int): BaseQuickHolder {
        val rv = getView<RecyclerView>(viewId)
        rv.layoutManager = LinearLayoutManager(mContext, if (direction == 1) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL, false)
        rv.adapter = adapter
        return this
    }
}