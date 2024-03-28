package com.ogh.support.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Looper
import android.text.TextUtils
import android.widget.ImageView
import com.blankj.utilcode.util.SizeUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.ogh.support.R
import java.io.File
import java.math.BigDecimal

/**
 * 图片处理类
 */
object GlideImageUtil {
    /**
     * 判断宿主是否存活
     */
    private fun isDestroy(context: Context?): Boolean {
        if (context == null) return true
        return if (context is Activity) context.isFinishing || context.isDestroyed else false
    }

    /**
     * 加载图片
     */
    fun showImage(context: Context, url: String, view: ImageView) {
        if (isDestroy(context)) return
        Glide.with(context)
            .load(url)
            .apply(getRequestOptions())
            .into(view)
    }

    /**
     * 播放Gif图
     */
    fun showGifImage(context: Context, url: String, view: ImageView) {
        if (isDestroy(context)) return
        Glide.with(context)
            .asGif()
            .load(url)
            .into(view)
    }

    /**
     * 播放Gif图
     */
    fun showGif2Image(context: Context, url: String, view: ImageView) {
        if (TextUtils.isEmpty(url) || isDestroy(context)) return
        if (url.endsWith(".gif")) {
            Glide.with(context)
                .asGif()
                .load(url)
                .into(view)
        } else {
            Glide.with(context)
                .load(url)
                .into(view)
        }
    }

    /**
     * 加载图片(跳过缓存,一般是频繁更换的图片)
     */
    fun showImageNoCache(context: Context, url: String, view: ImageView) {
        if (isDestroy(context)) return
        Glide.with(context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .apply(getRequestOptions()).into(view)
    }

    /**
     * 等比例缩放图片
     */
    fun showWrapImage(context: Context, url: String, view: ImageView) {
        if (isDestroy(context)) return
        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    view.setImageBitmap(resource)
                    val width = resource.width //获取原图的宽高
                    val height = resource.height
                    //获取imageView的宽
                    var imageViewWidth = view.width
                    val params = view.layoutParams
                    if (imageViewWidth <= 0) //修复等比例缩放bug
                        imageViewWidth = params.width
                    //计算缩放比例
                    val sy = (imageViewWidth * 0.1).toFloat() / (width * 0.1).toFloat()
                    //计算图片等比例放大后的高
                    params.height = (height * sy).toInt()
                    view.layoutParams = params
                }
            })
    }

    /**
     * 等比例缩放图片
     */
    fun showWrapImage2(context: Context, url: String, view: ImageView) {
        if (isDestroy(context)) return
        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    view.setImageBitmap(resource)
                    val params = view.layoutParams
                    params.width = resource.width
                    var height = resource.height
                    val maxHeight = SizeUtils.dp2px(350f)
                    if (height > maxHeight) height = maxHeight
                    params.height = height
                    view.layoutParams = params
                }
            })
    }

    private fun getRequestOptions() = RequestOptions().placeholder(R.mipmap.img_show_error).error(R.mipmap.img_show_error)

    /**
     * 清除图片磁盘缓存
     */
    private  fun clearImageDiskCache(context: Context?) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Thread { Glide.get(context!!).clearDiskCache() }.start()
            } else
                Glide.get(context!!).clearDiskCache()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 清除图片内存缓存
     */
    private fun clearImageMemoryCache(context: Context?) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) //只能在主线程执行
                Glide.get(context!!).clearMemory()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 清除图片所有缓存
     */
    fun clearImageAllCache(context: Context) {
        if (isDestroy(context)) return
        clearImageDiskCache(context)
        clearImageMemoryCache(context)
        val imageExternalCatchDir = context.externalCacheDir.toString() + ExternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR
        deleteFolderFile(imageExternalCatchDir, true)
    }

    /**
     * 获取Glide造成的缓存大小
     */
    fun getCacheSize(context: Context): String {
        try {
            return getFormatSize(getFolderSize(File(context.cacheDir.toString() + "/" + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR)).toDouble())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 获取指定文件夹内所有文件大小的和
     */
    private fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()
            if(fileList.isNullOrEmpty()){
                for (aFileList in fileList!!)
                    size = if (aFileList.isDirectory) size + getFolderSize(aFileList) else size + aFileList.length()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    /**
     * 删除指定目录下的文件，这里用于缓存的删除
     */
    private fun deleteFolderFile(filePath: String, deleteThisPath: Boolean) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                val file = File(filePath)
                if (file.isDirectory) {
                    val files = file.listFiles()
                    if(files.isNullOrEmpty()){
                        for (file1 in files!!)
                            deleteFolderFile(file1.absolutePath, true)
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory) {
                        file.delete()
                    } else {
                        val files = file.listFiles()
                        if(files.isNullOrEmpty())
                            file.delete()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 格式化单位
     */
    private fun getFormatSize(size: Double): String {
        val kiloByte = size / 1024
        if (kiloByte < 1)
            return size.toString() + "Byte"
        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(kiloByte.toString())
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB"
        }
        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(megaByte.toString())
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB"
        }
        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(gigaByte.toString())
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB"
        }
        val result4 = BigDecimal(teraBytes)
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB"
    }
}