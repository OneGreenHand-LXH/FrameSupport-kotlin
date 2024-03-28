package com.ogh.support.util

import android.content.Context
import android.os.*
import com.blankj.utilcode.util.*
import com.blankj.utilcode.util.FileUtils
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import java.io.File
import java.math.BigDecimal

/**
 * 缓存工具类
 */
object CleanDataUtils {
    /**
     * 获取缓存大小
     */
    fun getTotalCacheSize(context: Context): String {
        var cacheSize = FileUtils.getLength(context.cacheDir) //获取内部缓存大小
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) //sd卡正常挂载
            cacheSize += FileUtils.getLength(context.externalCacheDir) //获取外部缓存大小
        cacheSize += FileUtils.getLength(File(context.cacheDir.toString() + "/" + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR)) //获取glide的图片缓存大小
        return getFormatSize(cacheSize.toDouble())
    }

    /**
     * 清空缓存
     */
    fun clearAllCache() {
        CleanUtils.cleanInternalCache() //清除内部缓存
        CleanUtils.cleanExternalCache() //清除外部缓存
    }

    /**
     * 格式化单位
     */
    private fun getFormatSize(size: Double): String {
        val kiloByte = size / 1024
        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(kiloByte.toString())
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "K"
        }
        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(megaByte.toString())
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "M"
        }
        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(gigaByte.toString())
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "G"
        }
        val result4 = BigDecimal(teraBytes)
        return (result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "T")
    }
}