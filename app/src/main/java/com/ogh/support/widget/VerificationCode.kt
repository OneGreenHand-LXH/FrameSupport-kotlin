package com.ogh.support.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.blankj.utilcode.util.SizeUtils
import com.ogh.frame.base.BaseModel
import com.ogh.frame.base.BaseRequestView
import com.ogh.frame.bean.BaseBean
import com.ogh.frame.util.ToastUtil
import com.ogh.support.R
import com.ogh.support.api.API

/**
 * 验证码倒计时工具类
 */
@SuppressLint("AppCompatCustomView")
class VerificationCode : TextView, LifecycleObserver, BaseRequestView<BaseBean> {
    var conductColor = Color.RED //倒计时显示的颜色
    var endColor = Color.RED //倒计时结束时显示的颜色
    var endText = "重新获取" //结束时显示的文字
    var companyText = " s" //倒计时后面显示的单位
    var durationTime = 60000 //持续时间
    var intervalTime = 1000 //间隔时间
    private var countDownTimer: CountDownTimer? = null

    constructor (context: Context) : super(context) {
        initView(context, null)
    }

    constructor (context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor (context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    /**
     * 开始倒计时
     */
    fun start(phone: String, codeType: CodeType) {
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showShortToast("手机号不能为空")
        } else if (phone.startsWith("0") || phone.length != 11) {
            ToastUtil.showShortToast("手机号格式不正确")
        } else {
            BaseModel.Builder(this)
                .putParam("Mobile", phone)
                .putParam("VCType", codeType.value)
                .create().post(API.GET_WEN_ZHANG) //这里填写真实请求地址
        }
    }

    enum class CodeType(val value: Int) {
        LOGIN(1),  //登录
        REGISTER(2);//注册
    }

    /**
     * 初始化
     */
    private fun initView(context: Context, attrs: AttributeSet?) {
        if (context is AppCompatActivity) {
            if (!context.isFinishing && !context.isDestroyed) //注册绑定生命周期
                context.lifecycle.addObserver(this)
        }
        minWidth = SizeUtils.dp2px(95f)
        minHeight = SizeUtils.dp2px(30f)
        gravity = Gravity.CENTER
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.VerificationCode)
            conductColor = array.getColor(R.styleable.VerificationCode_VcConductColor, Color.RED)
            endColor = array.getColor(R.styleable.VerificationCode_VcEndColor, Color.RED)
            endText = array.getString(R.styleable.VerificationCode_VcEndText).toString()
            companyText = array.getString(R.styleable.VerificationCode_VcCompany).toString()
            durationTime = array.getInt(R.styleable.VerificationCode_VcDuration, 60000)
            intervalTime = array.getInt(R.styleable.VerificationCode_VcIntervalTime, 1000)
            if (TextUtils.isEmpty(companyText)) companyText = " s"
            if (TextUtils.isEmpty(endText)) endText = "重新获取"
            array.recycle()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
            countDownTimer = null
        }
    }

    override fun requestSuccess(data: BaseBean, tag: Any, pageIndex: Int, pageCount: Int) {
        countDownTimer = object : CountDownTimer(durationTime.toLong(), intervalTime.toLong()) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                isEnabled = false
                text = (millisUntilFinished / 1000).toString() + companyText
                setTextColor(conductColor)
            }

            override fun onFinish() {
                isEnabled = true
                text = endText
                setTextColor(endColor)
            }
        }
        ToastUtil.showShortToast("验证码已发送")
        countDownTimer!!.start() //开始倒计时
    }

    override fun requestFail(data: BaseBean, tag: Any) {
        ToastUtil.showShortToast(data.errorMsg)
    }

    override fun requestError(e: Throwable, tag: Any) {
        ToastUtil.showShortToast("请求出错")
    }

    override fun showLoadingView() {}

    override fun showEmptyView() {}

    override fun showNetErrorView(tips: String) {}

    override fun refreshView() {}

    override fun tokenOverdue() {}

    override fun showLoadingDialog(msg: String, isCancel: Boolean) {}

    override fun dismissLoadingDialog() {}
}