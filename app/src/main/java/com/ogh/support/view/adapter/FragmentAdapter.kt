package com.ogh.support.view.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.fragment.app.FragmentActivity

/**
 * viewpage2
 * https://developer.android.google.cn/training/animation/screen-slide-2?hl=zh-cn
 */
class FragmentAdapter : FragmentStateAdapter {
    private var mFragments: MutableList<Fragment>

    constructor(activity: FragmentActivity, mFragments: MutableList<Fragment>) : super(activity) {
        this.mFragments = mFragments
    }

    constructor(fragment: Fragment, mFragments: MutableList<Fragment>) : super(fragment) {
        this.mFragments = mFragments
    }

    override fun createFragment(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getItemCount(): Int {
        return mFragments.size
    }
}