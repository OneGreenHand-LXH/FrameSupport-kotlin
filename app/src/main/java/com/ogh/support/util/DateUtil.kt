package com.ogh.support.util

import java.util.*

/**
 * description: 所有关于时间计算的工具类
 */
object DateUtil {
    /**
     * 设置时间
     */
    fun setCalendar(year: Int, month: Int, date: Int): Calendar {
        val cl = Calendar.getInstance()
        cl.set(year, month - 1,date)
        return cl
    }

    /**
     * 获取当前时间的前一天时间
     */
    fun getBeforeDay(cl: Calendar): Calendar {
        val day = cl.get(Calendar.DATE) //使用set方法直接进行设置
        cl.set(Calendar.DATE,day - 1)
        return cl
    }

    /**
     * 获取当前时间的后一天时间
     */
    fun getAfterDay(cl: Calendar): Calendar {
        val day = cl.get(Calendar.DATE) //使用set方法直接设置时间值
        cl.set(Calendar.DATE,day + 1)
        return cl
    }

    /**
     * 获取当前时间的后30天时间
     */
    fun getAfter30Day(cl: Calendar): Calendar {
        val day = cl.get(Calendar.DATE) //使用set方法直接设置时间值
        cl.set(Calendar.DATE,day + 30)
        return cl
    }

    /**
     * 得到几天前的时间
     */
    fun getDateBefore(d: Date, day: Int): Date {
        val now = Calendar.getInstance()
        now.time = d
        now.set(Calendar.DATE,now.get(Calendar.DATE)-day)
        return now.time
    }

    /**
     * 得到几天后的时间
     */
    fun getDateAfter(d: Date, day: Int): Date {
        val now = Calendar.getInstance()
        now.time = d
        now.set(Calendar.DATE,now.get(Calendar.DATE)+day)
        return now.time
    }
}