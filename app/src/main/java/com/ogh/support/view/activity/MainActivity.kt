package com.ogh.support.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.AppUtils
import com.google.android.material.navigation.NavigationBarView
import com.ogh.frame.base.activity.BaseActivity
import com.ogh.frame.util.ToastUtil
import com.ogh.support.R
import com.ogh.support.databinding.ActivityMainBinding
import com.ogh.support.util.ChannelUtils
import com.ogh.support.view.adapter.FragmentAdapter
import com.ogh.support.view.fragment.HomeFragment
import com.ogh.support.view.fragment.MineFragment
import java.util.ArrayList

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun init(savedInstanceState: Bundle?) {
        if (!this.isTaskRoot) { //第一次安装成功点击“打开”后Home键切出应用后再点击桌面图标返回导致应用重启问题(在配置了Intent.ACTION_MAIN的Activity中添加)
            val mainIntent: Intent = intent
            val action: String? = mainIntent.action
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN == action) {
                finish()
                return
            }
        }
        val mFragment: MutableList<Fragment> = ArrayList()
        mFragment.add(HomeFragment())
        mFragment.add(MineFragment())
        //viewBinding.viewPager.isUserInputEnabled=false;//禁止滑动
        // viewBinding.viewPager.offscreenPageLimit=mFragment.size;//设置缓存,数量超过2可设置
        viewBinding.viewPager.adapter=FragmentAdapter(this, mFragment)
        initViewPagerChangeListener()
        viewBinding.navigationView.setOnItemSelectedListener(object : NavigationBarView.OnItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.home -> {
                        viewBinding.viewPager.setCurrentItem(0, false)
                    }
                    R.id.core -> {
                        return false
                    }
                    R.id.mine -> viewBinding.viewPager.setCurrentItem(1, false)
                }
                return true
            }
        })
        viewBinding.floatingAction.setOnClickListener{
            ToastUtil.showShortToast("当前渠道号为: " + ChannelUtils.getChannel())
        }
    }

    private fun initViewPagerChangeListener() {
        viewBinding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                var newPosition = position
                super.onPageSelected(newPosition)
                if (newPosition == 1) //第二个为悬浮按钮
                    newPosition++
                val menuItem: MenuItem = viewBinding.navigationView.menu.getItem(newPosition)
                menuItem.isChecked = true
            }
        })
    }

    private var firstTime: Long = 0 //记录用户首次点击返回键的时间

   override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event?.action == KeyEvent.ACTION_DOWN) {
            val secondTime = System.currentTimeMillis()
            if (secondTime - firstTime > 2000) {
                ToastUtil.showShortToast("再按一次退出")
                firstTime = secondTime
                return true
            } else AppUtils.exitApp()
        }
        return super.onKeyDown(keyCode, event)
    }
}