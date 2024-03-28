package com.ogh.support.widget

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.ogh.frame.util.CustomClickListener
import com.ogh.support.R

/**
 * @describe 通用标题栏
 */
class TitleBarLayout : LinearLayout {
    private var mBackgroundColor = Color.WHITE //背景颜色
    private var backImage = R.mipmap.back_black //左侧返回图标
    private var backShow = true //返回图标是否显示
    private var tText: String? = resources.getString(R.string.app_name) //标题文字
    private var tTextColor = Color.BLACK //标题文字颜色
    private var rTextIsShow = false//右侧文字是否显示
    private var rText: String? = null//右侧文字
    private var rTextColor = Color.BLACK //右侧文字颜色
    //控件
    lateinit var titleText: TextView
    lateinit var otherText: TextView

    //点击事件
    private var clickListener: CustomClickListener? = null//返回按钮

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.TitleBarLayout)
            mBackgroundColor = array.getColor(R.styleable.TitleBarLayout_TblBackgroundColor, Color.WHITE)
            backShow = array.getBoolean(R.styleable.TitleBarLayout_TblBackShow, true)
            backImage = array.getResourceId(R.styleable.TitleBarLayout_TblBackImage, R.mipmap.back_black)
            tText = array.getString(R.styleable.TitleBarLayout_TblText)
            tTextColor = array.getColor(R.styleable.TitleBarLayout_TblTextColor, Color.BLACK)
            rTextIsShow = array.getBoolean(R.styleable.TitleBarLayout_TblRTextShow, false)
            rText = array.getString(R.styleable.TitleBarLayout_TblRText)
            rTextColor = array.getColor(R.styleable.TitleBarLayout_TblRTextColor, Color.BLACK)
            array.recycle()
        }
        findId()
    }

    private fun findId() {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_titlebar, this)
        val layoutTitle = view.findViewById<RelativeLayout>(R.id.layout_title)
        val imgFinish = view.findViewById<ImageView>(R.id.img_finish)
        titleText = view.findViewById(R.id.app_title)
        otherText = view.findViewById(R.id.other)
        layoutTitle.setBackgroundColor(mBackgroundColor)
        if (backShow) {
            imgFinish.visibility = VISIBLE
            imgFinish.setImageResource(backImage)
        } else imgFinish.visibility = GONE
        titleText.text = if (TextUtils.isEmpty(tText)) resources.getString(R.string.app_name) else tText
        titleText.setTextColor(tTextColor)
        if (rTextIsShow) {
            otherText.visibility = VISIBLE
            otherText.text = if (TextUtils.isEmpty(rText)) "确定" else rText
            otherText.setTextColor(rTextColor)
        } else otherText.visibility = GONE
        imgFinish.setOnClickListener(object : CustomClickListener() {
            override fun onSingleClick(v: View) {
                if (clickListener != null) {
                    clickListener!!.onClick(v)
                    return
                }
                if (context is Activity) (context as Activity).finish()
            }
        })
    }

    interface BackClickListener {
        fun onClick(view: View)
    }

    fun setOnBackListener(clickListener: CustomClickListener?) {
        this.clickListener = clickListener
    }

    fun setOnRightListener(clickListener: CustomClickListener?) {
        otherText.setOnClickListener(clickListener)
    }

    fun setTitle(str: String) {
        titleText.text = str
    }

    fun setRightTitle(str: String) {
        otherText.visibility = VISIBLE
        otherText.text = str
    }

    fun setRightIsShow(isShow: Boolean) {
        otherText.visibility = if (isShow) VISIBLE else GONE
    }
}