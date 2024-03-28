package com.ogh.support.util

import java.math.BigDecimal

/**
 * description: 高精度计算工具类
 * https://www.cnblogs.com/zhangyinhua/p/11545305.html
 */
object CalculUtil {
    /**
     * 提供精确的加法运算
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    fun add(v1: Double, v2: Double): Double {
        val b1 = BigDecimal(v1.toString())
        val b2 = BigDecimal(v2.toString())
        return b1.add(b2).toDouble()
    }

    /**
     * 提供精确的加法运算
     * ROUND_HALF_UP 四舍五入
     * ROUND_FLOOR 不四舍五入
     *
     * @param v1    被加数
     * @param v2    加数
     * @param scale 保留scale 位小数
     * @return 两个参数的和
     */
    fun add(v1: String, v2: String, scale: Int): String {
        require(scale >= 0) { "保留的小数位数必须大于零" }
        val b1 = BigDecimal(v1)
        val b2 = BigDecimal(v2)
        return b1.add(b2).setScale(scale, BigDecimal.ROUND_FLOOR).toPlainString()
    }

    /**
     * 提供精确的减法运算
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    fun sub(v1: Double, v2: Double): Double {
        val b1 = BigDecimal(v1.toString())
        val b2 = BigDecimal(v2.toString())
        return b1.subtract(b2).toDouble()
    }

    /**
     * 提供精确的减法运算
     * ROUND_HALF_UP 四舍五入
     * ROUND_FLOOR 不四舍五入
     *
     * @param v1    被减数
     * @param v2    减数
     * @param scale 保留scale 位小数
     * @return 两个参数的差
     */
    fun sub(v1: String?, v2: String?, scale: Int): String {
        require(scale >= 0) { "保留的小数位数必须大于零" }
        val b1 = BigDecimal(v1)
        val b2 = BigDecimal(v2)
        return b1.subtract(b2).setScale(scale, BigDecimal.ROUND_FLOOR).toPlainString()
    }

    /**
     * 提供精确的乘法运算
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    fun mul(v1: Double, v2: Double): Double {
        val b1 = BigDecimal(v1.toString())
        val b2 = BigDecimal(v2.toString())
        return b1.multiply(b2).toDouble()
    }

    /**
     * 提供精确的乘法运算
     * ROUND_HALF_UP 四舍五入
     * ROUND_FLOOR 不四舍五入
     *
     * @param v1    被乘数
     * @param v2    乘数
     * @param scale 保留scale 位小数
     * @return 两个参数的积
     */
    fun mul(v1: String, v2: String, scale: Int): String {
        require(scale >= 0) { "保留的小数位数必须大于零" }
        val b1 = BigDecimal(v1)
        val b2 = BigDecimal(v2)
        return b1.multiply(b2).setScale(scale, BigDecimal.ROUND_FLOOR).toPlainString()
    }

    /**
     * 提供精确的除法运算
     * ROUND_HALF_UP 四舍五入
     * ROUND_FLOOR 不四舍五入
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 保留scale 位小数
     * @return 两个参数的商
     */
    fun div(v1: Double, v2: Double, scale: Int): Double {
        require(scale >= 0) { "保留的小数位数必须大于零" }
        val b1 = BigDecimal(v1.toString())
        val b2 = BigDecimal(v2.toString())
        return b1.divide(b2, scale, BigDecimal.ROUND_FLOOR).toDouble()
    }

    /**
     * 提供精确的除法运算
     * ROUND_HALF_UP 四舍五入
     * ROUND_FLOOR 不四舍五入
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 保留scale 位小数
     * @return 两个参数的商
     */
    fun div(v1: String?, v2: String?, scale: Int): String {
        require(scale >= 0) { "保留的小数位数必须大于零" }
        val b1 = BigDecimal(v1)
        val b2 = BigDecimal(v2)
        return b1.divide(b2, scale, BigDecimal.ROUND_FLOOR).toPlainString()
    }

    /**
     * 提供精确的小数位处理
     * ROUND_HALF_UP 四舍五入
     * ROUND_FLOOR 不四舍五入
     *
     * @param v     需要四舍五入的数字
     * @param scale 保留scale 位小数
     * @return 四舍五入后的结果
     */
    fun round(v: Double, scale: Int): Double {
        require(scale >= 0) { "保留的小数位数必须大于零" }
        val b = BigDecimal(v.toString())
        return b.setScale(scale, BigDecimal.ROUND_FLOOR).toDouble()
    }

    /**
     * 提供精确的小数位处理
     * ROUND_HALF_UP 四舍五入
     * ROUND_FLOOR 不四舍五入
     *
     * @param v     需要四舍五入的数字
     * @param scale 保留scale 位小数
     * @return 四舍五入后的结果
     */
    fun round(v: String, scale: Int): String {
        require(scale >= 0) { "保留的小数位数必须大于零" }
        val b = BigDecimal(v)
        return b.setScale(scale, BigDecimal.ROUND_FLOOR).toPlainString()
    }

    /**
     * 取余数
     * ROUND_HALF_UP 四舍五入
     * ROUND_FLOOR 不四舍五入
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 保留scale 位小数
     * @return 余数
     */
    fun remainder(v1: String, v2: String, scale: Int): String {
        require(scale >= 0) { "保留的小数位数必须大于零" }
        val b1 = BigDecimal(v1)
        val b2 = BigDecimal(v2)
        return b1.remainder(b2).setScale(scale, BigDecimal.ROUND_FLOOR).toPlainString()
    }

    /**
     * 比较大小
     *
     * @param v1 被比较数
     * @param v2 比较数
     * @return 如果v1 大于v2 则 返回true 否则false
     */
    fun compare(v1: String, v2: String): Boolean {
        val b1 = BigDecimal(v1)
        val b2 = BigDecimal(v2)
        val bj = b1.compareTo(b2)
        return bj > 0
    }
}