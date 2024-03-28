package com.ogh.frame.request

import okhttp3.Interceptor
import okhttp3.Response
import java.net.URLDecoder

/**
 * description: 网络请求日志拦截
 */
class HttpLoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = URLDecoder.decode(request.url.toString(), "UTF-8")
        //添加统一通用header，不会覆盖前面的header
        val requestBuilder = request
            .newBuilder()
            .addHeader("Accept", "application/json")
            .url(url)
        return chain.proceed(requestBuilder.build())
    }

}