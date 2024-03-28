package com.ogh.frame.loadingView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class VaryViewHelper(view: View) : IVaryViewHelper {
    private var mView: View = view
    private var parentView: ViewGroup? = null
    private var params: ViewGroup.LayoutParams? = null
    private lateinit var currentView: View
    private var viewIndex = 0

    private fun init() {
        params = mView.layoutParams
        parentView = if (null != mView.parent) {
            mView.parent as ViewGroup
        } else
            mView.rootView.findViewById<View>(android.R.id.content) as ViewGroup
        val childCount = parentView!!.childCount
        for (index in 0 until childCount) {
            if (mView == parentView!!.getChildAt(index)) {
                viewIndex = index
                break
            }
        }
        currentView = mView
    }

    override fun getCurrentLayout(): View {
        return currentView
    }

    override fun restoreView() {
        showLayout(mView)
    }

    override fun showLayout(view: View) {
        if (null == parentView) init()
        currentView = view
        if (parentView!!.getChildAt(viewIndex) !== view) {
            var parent :ViewGroup ?=null
            if(null != view.parent) parent = view.parent as ViewGroup
            parent?.removeView(view)
            parentView!!.removeViewAt(viewIndex)
            parentView!!.addView(view, viewIndex, params)
        }
    }

    override fun inflate(layoutId: Int): View {
        return LayoutInflater.from(mView.context).inflate(layoutId, null)
    }

    override fun getContext(): Context {
        return mView.context
    }

    override fun getView(): View {
        return mView
    }
}