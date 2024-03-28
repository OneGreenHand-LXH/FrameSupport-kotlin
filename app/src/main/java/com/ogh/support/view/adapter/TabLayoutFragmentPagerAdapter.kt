package com.ogh.support.view.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.fragment.app.FragmentActivity

class TabLayoutFragmentPagerAdapter : FragmentStateAdapter {
    private var mFragments: List<Fragment>
    private var titles: List<String>

    constructor(activity: FragmentActivity, fragments: List<Fragment>, titles: List<String>) : super(activity) {
        this.mFragments = fragments
        this.titles = titles
    }

    constructor(fragment: Fragment, fragments: List<Fragment>, titles: List<String>) : super(fragment) {
        this.mFragments = fragments
        this.titles = titles
    }

    override fun createFragment(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getItemCount(): Int {
        return mFragments.size
    }
}