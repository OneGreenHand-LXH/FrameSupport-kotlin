package com.ogh.support.view.fragment

import android.os.Bundle
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.NetworkUtils
import com.ogh.frame.base.fragment.BaseFragment
import com.ogh.frame.util.ToastUtil
import com.ogh.support.config.AppConfig
import com.ogh.support.databinding.FragmentHomeBinding
import com.ogh.support.util.InstructionsUtils
import com.ogh.support.view.activity.WebActivity

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override fun init(savedInstanceState: Bundle?) {
        setViewClicked()
    }

    private fun setViewClicked() {
        viewBinding.videoWeb.setOnClickListener{
            WebActivity.openActivity(mActivity)
        }
        viewBinding.downloadApk.setOnClickListener{
            if (!NetworkUtils.isWifiConnected()) {
                ToastUtil.showShortToast("请在wifi状态下下载")
            } else {
                // if (CommonUtil.notificationAuthority(mActivity)) {//检测通知栏是否打开,未打开类似于静默下载
                InstructionsUtils.checkInstallOrDown(mActivity, "https://downv6.qq.com/qqweb/QQ_1/android_apk/TIM_3.5.6.3208_64.apk")
                //Tim下载地址,大概110Mb
                //   }
            }
        }
        viewBinding.clearDownloadApk.setOnClickListener{
            if (FileUtils.isFileExists(AppConfig.FilePath.FILE_FOLDER + "TIM_3.5.6.3208_64.apk")) {
                if (FileUtils.delete(AppConfig.FilePath.FILE_FOLDER + "TIM_3.5.6.3208_64.apk")) {
                    ToastUtil.showShortToast("文件已删除,可以重新下载了")
                } else ToastUtil.showShortToast("文件删除失败")
            } else ToastUtil.showShortToast("无需清除")
        }
    }
}