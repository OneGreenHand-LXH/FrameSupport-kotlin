package com.ogh.frame.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import autodispose2.AutoDispose
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.ogh.frame.R
import com.ogh.frame.FrameApplication.Companion.getContext
import com.ogh.frame.bean.BaseBean
import com.ogh.frame.bean.FileInfoBean
import com.ogh.frame.config.BaseConfig
import com.ogh.frame.request.HttpRequest
import com.ogh.frame.util.GsonUtil
import com.ogh.frame.util.ToastUtil
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import okhttp3.ResponseBody

/**
 * 网络层
 */
class BaseModel(builder: Builder) {

    private var mHttpRequest: HttpRequest = HttpRequest()
    var mBuilder: Builder = builder
    var mIsEmpty = false

    fun post(api: String) {
        post(api, BaseBean::class.java)
    }

    fun get(api: String) {
        get(api, BaseBean::class.java)
    }

    fun <B : BaseBean> post(api: String, clazz: Class<B>) {
        val responseBodySubscriber: Observer<ResponseBody> = getResponseBodySubscriber(api, clazz) ?: return
        when (mBuilder.mBaseRequestView) {
            is FragmentActivity -> { //绑定生命周期
                mHttpRequest.post(api, mBuilder.mParam)
                    ?.to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(mBuilder.mBaseRequestView as FragmentActivity, Lifecycle.Event.ON_DESTROY)))
                    ?.subscribe(responseBodySubscriber)
            }
            is Fragment -> {
                mHttpRequest.post(api, mBuilder.mParam)
                    ?.to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(mBuilder.mBaseRequestView as Fragment, Lifecycle.Event.ON_DESTROY)))
                    ?.subscribe(responseBodySubscriber)
            }
            else -> mHttpRequest.post(api, mBuilder.mParam)?.subscribe(responseBodySubscriber)
        }
    }

    fun <B : BaseBean> get(api: String, clazz: Class<B>) {
        val responseBodySubscriber: Observer<ResponseBody> = getResponseBodySubscriber(api, clazz) ?: return
        when (mBuilder.mBaseRequestView) {
            is FragmentActivity -> {
                mHttpRequest.get(api)
                    ?.to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(mBuilder.mBaseRequestView as FragmentActivity, Lifecycle.Event.ON_DESTROY)))
                    ?.subscribe(responseBodySubscriber)
            }
            is Fragment -> {
                mHttpRequest.get(api)
                    ?.to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(mBuilder.mBaseRequestView as Fragment, Lifecycle.Event.ON_DESTROY)))
                    ?.subscribe(responseBodySubscriber)
            }
            else -> mHttpRequest.get(api)
                ?.subscribe(responseBodySubscriber)
        }
    }

    fun <B : BaseBean> upload(api: String, clazz: Class<B>) {
        val responseBodySubscriber: Observer<ResponseBody> = getResponseBodySubscriber(api, clazz) ?: return
        when (mBuilder.mBaseRequestView) {
            is FragmentActivity -> {
                mHttpRequest
                    .uploadFile(api, mBuilder.mParam, mBuilder.multiFileKey, mBuilder.mFileInfoBeans)
                    ?.to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(mBuilder.mBaseRequestView as FragmentActivity, Lifecycle.Event.ON_DESTROY)))
                    ?.subscribe(responseBodySubscriber)
            }
            is Fragment -> {
                mHttpRequest
                    .uploadFile(api, mBuilder.mParam, mBuilder.multiFileKey, mBuilder.mFileInfoBeans)
                    ?.to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(mBuilder.mBaseRequestView as Fragment, Lifecycle.Event.ON_DESTROY)))
                    ?.subscribe(responseBodySubscriber)
            }
            else -> mHttpRequest
                .uploadFile(api, mBuilder.mParam, mBuilder.multiFileKey, mBuilder.mFileInfoBeans)
                ?.subscribe(responseBodySubscriber)
        }
    }

    private fun <B : BaseBean> getResponseBodySubscriber(tag: String, clazz: Class<B>): Observer<ResponseBody>? {
        if (!NetworkUtils.isWifiConnected() && !NetworkUtils.isConnected()) { //检查网络是否连接
            if (mBuilder.mBaseRequestView is BaseSwipeView<*>) {
                (mBuilder.mBaseRequestView as BaseSwipeView<*>).resetRefreshView()
                if (mBuilder.pageIndex != BaseConfig.ViewPage.START_INDEX)
                    (mBuilder.mBaseRequestView as BaseSwipeListView<*>).loadMoreFailView()
            }
            if (mBuilder.mLoadStyle == LoadStyle.DIALOG_VIEW || mBuilder.mLoadStyle == LoadStyle.VIEW)
                mBuilder.mBaseRequestView.showNetErrorView("")
            ToastUtil.showShortToast(getContext().resources.getString(R.string.frame_check_network))
            return null
        }
        return object : Observer<ResponseBody> {
            override fun onError(e: Throwable) {
                LogUtils.e("okhttp", "请求错误")
                refreshStatusView(e, mBuilder.requestTag ?: tag)
            }

            override fun onComplete() {
                when (mBuilder.mLoadStyle) {
                    LoadStyle.NONE -> {}
                    LoadStyle.VIEW -> if (mIsEmpty) mBuilder.mBaseRequestView.showEmptyView() else mBuilder.mBaseRequestView.refreshView()
                    LoadStyle.DIALOG -> mBuilder.mBaseRequestView.dismissLoadingDialog()
                    LoadStyle.DIALOG_VIEW -> {
                        if (mIsEmpty) mBuilder.mBaseRequestView.showEmptyView() else mBuilder.mBaseRequestView.refreshView()
                        mBuilder.mBaseRequestView.dismissLoadingDialog()
                    }
                }
                if (mBuilder.mBaseRequestView is BaseSwipeView) (mBuilder.mBaseRequestView as BaseSwipeView).resetRefreshView()
            }

            override fun onSubscribe(d: Disposable) {
                when (mBuilder.mLoadStyle) {
                    LoadStyle.NONE -> {
                        return
                    }
                    LoadStyle.VIEW -> {
                        mBuilder.mBaseRequestView.showLoadingView()
                    }
                    else -> mBuilder.mBaseRequestView.showLoadingDialog(mBuilder.mMsg, mBuilder.isDialogCancel)
                }
            }

            override fun onNext(requestBody: ResponseBody) {
                try { //解析json
                    val bean: B  = GsonUtil.getBean(requestBody.string(), clazz)
                    mIsEmpty = bean.isEmpty()
                    if (bean.errorCode == 0) {
                        mBuilder.mBaseRequestView.requestSuccess(bean, mBuilder.requestTag ?: tag, mBuilder.pageIndex, mBuilder.pageCount)
                    } else
                        mBuilder.mBaseRequestView.requestFail(bean, mBuilder.requestTag ?: tag)
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (BaseConfig.DEBUG) {
                        LogUtils.e("数据解析异常", e.message)
                        throw RuntimeException("数据解析异常：" + e.message)
                    } else mBuilder.mBaseRequestView.requestError(e, mBuilder.requestTag ?: tag)
                }
            }
        }
    }

    fun refreshStatusView(e: Throwable, tag: Any) {
        when (mBuilder.mLoadStyle) {
            LoadStyle.NONE -> {}
            LoadStyle.VIEW -> mBuilder.mBaseRequestView.showNetErrorView(getContext().resources.getString(R.string.frame_server_error) + e.message)
            LoadStyle.DIALOG -> mBuilder.mBaseRequestView.dismissLoadingDialog()
            LoadStyle.DIALOG_VIEW -> {
                mBuilder.mBaseRequestView.dismissLoadingDialog()
                mBuilder.mBaseRequestView.showNetErrorView(getContext().resources.getString(R.string.frame_server_error) + e.message)
            }
        }
        mBuilder.mBaseRequestView.requestError(e, tag)
        if (mBuilder.mBaseRequestView is BaseSwipeView) {
            (mBuilder.mBaseRequestView as BaseSwipeView).resetRefreshView()
            if (mBuilder.pageIndex != BaseConfig.ViewPage.START_INDEX)
                (mBuilder.mBaseRequestView as BaseSwipeListView).loadMoreFailView()
        }
    }

    //构建模式--用于添加配置
    class Builder(baseRequestView: BaseRequestView<*>) {
        //默认参数
        var mLoadStyle = LoadStyle.NONE
        var mMsg = ""
        var requestTag: Any? = null
        var isDialogCancel = true //请求时dialog是否可以手动取消
        //动态参数
        var mParam: MutableMap<String, Any>? = null
        var mBaseRequestView: BaseRequestView<*> =baseRequestView
        //上传文件
        lateinit var mFileInfoBeans: MutableList<FileInfoBean>
        var multiFileKey = ""
        var pageCount = BaseConfig.ViewPage.PAGE_COUNT //每页请求的数据量
        var pageIndex = BaseConfig.ViewPage.START_INDEX //当前页码数

        fun create(): BaseModel {
            return BaseModel(this)
        }

        //设置加载风格
        fun setLoadStyle(loadStyle: LoadStyle): Builder {
            mLoadStyle = loadStyle
            return this
        }

        //添加参数
        fun putParam(key: String, value: Any): Builder {
            if (null == mParam)
                mParam = mutableMapOf()
            mParam!![key] = value
            return this
        }

        fun putAllParam(map: MutableMap<String, Any>): Builder {
            if (null == mParam)
                mParam = mutableMapOf()
            mParam!!.putAll(map)
            return this
        }

        //添加文件
        fun setFileInfoBeans(fileInfoBeans: MutableList<FileInfoBean>): Builder {
            mFileInfoBeans = fileInfoBeans
            return this
        }

        //多文件上传key（与后台约定）
        fun setMultiFileKey(multiFileKey: String): Builder {
            this.multiFileKey = multiFileKey
            return this
        }

        //设置加载框文字
        fun setLoadMsg(msg: String): Builder {
            mMsg = msg
            return this
        }

        //设置请求标题(用于区分请求)
        fun setRequestTag(requestTag: Any): Builder {
            this.requestTag = requestTag
            return this
        }

        //加载弹框是否可以手动取消
        fun setDialogCancel(dialogCancel: Boolean): Builder {
            isDialogCancel = dialogCancel
            return this
        }

        //设置一页请求请求数
        fun setPageCount(count: Int): Builder {
            pageCount = count
            return this
        }

        //设置当前请求页数
        fun setPageIndex(page: Int): Builder {
            pageIndex = page
            return this
        }
    }

    //加载样式
    enum class LoadStyle(val value: Int) {
        NONE(1),  //静默加载
        DIALOG(2),  //有加载框
        VIEW(3),  //有错误、网络异常等布局
        DIALOG_VIEW(4)
    }

}