package com.ogh.frame.request

import android.text.TextUtils
import com.ogh.frame.bean.FileInfoBean
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody

class HttpRequest {
   private var apiService: APIService = RetrofitWrapper.getInstance().createApi(APIService::class.java)

    fun get(url: String): Observable<ResponseBody>? {
        return apiService
            .get(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun post(url: String, any: Any?): Observable<ResponseBody>? {
        return apiService
           .post(url, any ?: "")
           .subscribeOn(Schedulers.io())
           .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * @param multipleFileKey 多图的key
     */
    fun uploadFile(url: String, params: Map<String, Any>?, multipleFileKey: String?, fileList: List<FileInfoBean>): Observable<ResponseBody>? {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        if (!params.isNullOrEmpty()) {
            for (key in params.keys)  //添加请求参数
                builder.addFormDataPart(key, params[key].toString())
        }
        if (!TextUtils.isEmpty(multipleFileKey)) { //类型: photos[0]、photos[1]、photos[2]
            fileList.forEachIndexed { index, item ->
                val asRequestBody = item.file.asRequestBody("image/*".toMediaTypeOrNull())
                builder.addFormDataPart("$multipleFileKey[$index]", item.file.name, asRequestBody)
            }
        } else { //类型: pic1、pic2、pic3
                for (item in fileList) {
                    val asRequestBody = item.file.asRequestBody("image/*".toMediaTypeOrNull())
                    builder.addFormDataPart(item.paramName, item.file.name, asRequestBody)
                }
        }
        return apiService
           .upload(url, builder.build())
           .subscribeOn(Schedulers.io())
           .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 下载文件
     */
    fun downloadFile(url: String, observer: Observer<ResponseBody>) {
        apiService
           .downloadFile(url)
           .subscribeOn(Schedulers.io())
           .subscribe(observer)
    }

}