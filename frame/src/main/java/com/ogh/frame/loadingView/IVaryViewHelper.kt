package com.ogh.frame.loadingView

import android.content.Context
import android.view.View


interface IVaryViewHelper {

    fun getCurrentLayout(): View

    fun restoreView()

    fun showLayout(view: View)

    fun inflate(layoutId: Int): View

    fun getContext(): Context

    fun getView(): View
}