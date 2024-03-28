package com.ogh.frame.request

import io.reactivex.rxjava3.core.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * description: API接口
 */
interface APIService {

    //下载文件
    @Streaming //防止写入内存
    @GET
    fun downloadFile(@Url fileUrl: String): Observable<ResponseBody>

    @GET
    fun get(@Url url: String): Observable<ResponseBody>

    @POST
    fun post(@Url url: String, @Body value: Any): Observable<ResponseBody>

    @POST
    fun upload(@Url url: String, @Body Body: RequestBody): Observable<ResponseBody>
}