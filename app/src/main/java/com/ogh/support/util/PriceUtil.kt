package com.ogh.support.util

import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import java.math.BigDecimal
import java.util.regex.Pattern

/**
 * 数字相关转换工具
 */
object PriceUtil {
    /**
     * 得到转化后的数值
     * 性能较好,精度低
     * digit 小数点后保留几位（四舍五入）
     */
    fun getPerformanceNumber(num: Double, digit: Int): String {
        return if (digit < 0) "0" else String.format("%." + digit + "f", num)
    }

    /**
     * 得到转化后的数值
     * 性能较差,精度高
     * digit 小数点后保留几位（价格为正:数值不变，价格为负:数值变小）
     */
    fun getAccurateNumber(num: String, digit: Int): Double {
        return if (digit < 0) 0.0 else BigDecimal(num).setScale(digit, BigDecimal.ROUND_FLOOR).toDouble()
    }

    /**
     * 四舍五入计算
     * 性能较差
     * digit 小数点后保留几位
     */
    fun rounding(num: String, digit: Int): Double {
        return BigDecimal(num).setScale(digit, BigDecimal.ROUND_HALF_UP).toDouble()
    }

    /**
     * 千位分隔符 10000会变成10,000
     * 小数点后的数字不会参与分割
     * digit 每隔几位分割
     */
    fun qianWeiFenGe(num: String, digit: Int): String {
        if (TextUtils.isEmpty(num)) return "0"
        var mNum = num
        var decimal = ""
        if (mNum.contains(".")) { //有小数点
            decimal = mNum.substring(mNum.indexOf(".")) //小数点后的数字
            mNum = mNum.substring(0, mNum.indexOf(".")) //小数点前的数字
        }
        return try {
            // ① 把串倒过来
            val tmp = StringBuffer().append(mNum).reverse()
            // ② 替换这样的串：连续split位数字的串，其右边还有个数字，在串的右边添加逗号
            val retNum =
                Pattern.compile("(\\d{" + 3 + "})(?=\\d)").matcher(tmp.toString()).replaceAll("$1,")
            // ③ 替换完后，再把串倒回去返回
            val resultNum = StringBuffer().append(retNum).reverse().toString()
            if (!TextUtils.isEmpty(decimal)) subZeroAndDot(resultNum + decimal) else resultNum
        } catch (e: Exception) {
            e.printStackTrace()
            if (!TextUtils.isEmpty(decimal)) subZeroAndDot(mNum + decimal) else mNum
        }
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     */
    fun subZeroAndDot(s: String): String {
        var str = s
        if (TextUtils.isEmpty(str) || str.trim().isEmpty()) {
            return "0"
        } else if (s.indexOf(".") > 0) {
           str = str.replace("0+?$".toRegex(), "") //去掉多余的0
           str = str.replace("[.]$".toRegex(), "") //如最后一位是.则去掉
        }
        return str
    }

    fun setPriceSp(text1: String, text2: String, font: Int, color1: Int, color2: Int): Spannable { //双色同字号
        val sp: Spannable = SpannableString(text1 + text2)
        sp.setSpan(AbsoluteSizeSpan(font, true), 0, text1.length + text2.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        sp.setSpan(ForegroundColorSpan(color1), 0, text1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        sp.setSpan(ForegroundColorSpan(color2), text1.length, text1.length + text2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sp
    }

    fun setPriceSp2(text1: String, text2: String, font1: Int, font2: Int, color: Int): Spannable { //单色不同字号
        val sp: Spannable = SpannableString(text1 + text2)
        sp.setSpan(AbsoluteSizeSpan(font1, true), 0, text1.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        sp.setSpan(AbsoluteSizeSpan(font2, true), text1.length, text1.length + text2.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        sp.setSpan(ForegroundColorSpan(color), 0, text1.length + text2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sp
    }

    fun setPriceSp(text1: String, text2: String, font1: Int, font2: Int, color1: Int, color2: Int): Spannable { //双色不同字号
        val sp: Spannable = SpannableString(text1 + text2)
        sp.setSpan(AbsoluteSizeSpan(font1, true), 0, text1.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        sp.setSpan(AbsoluteSizeSpan(font2, true), text1.length, text1.length + text2.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        sp.setSpan(ForegroundColorSpan(color1), 0, text1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        sp.setSpan(ForegroundColorSpan(color2), text1.length, text1.length + text2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sp
    }
}