package com.ogh.frame.request

import com.ogh.frame.config.BaseConfig
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * description: Retrofit网络请求工具类
 */
class RetrofitWrapper {
    private var retrofit: Retrofit? = null

    companion object {
        private var NOW_TIME_OUT = 10 //当前的超时时长
        private const val ALL_TIME_OUT = 10 //总超时时长
        private var instance: RetrofitWrapper? = null

        fun getInstance(time: Int, isRelease: Boolean): RetrofitWrapper {
            if (isRelease || NOW_TIME_OUT != ALL_TIME_OUT) //需要重置或超时时长不等于10秒(目的是为了仅单次重置有效)
                instance = null
            if (instance == null) {
                synchronized(this) {
                    if (instance == null)
                        instance = RetrofitWrapper(time)
                }
            }
            return instance!!
        }

        fun getInstance(): RetrofitWrapper {
            return getInstance(ALL_TIME_OUT, false)
        }

    }

    private  constructor()

    private constructor(time: Int) {
        NOW_TIME_OUT = time
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(time.toLong(), TimeUnit.SECONDS)
            .readTimeout(time.toLong(), TimeUnit.SECONDS)
            .writeTimeout(time.toLong(), TimeUnit.SECONDS)
            //.addInterceptor(HttpLoggingInterceptor())//此处设置的拦截器用来添加统一的请求头
            //.addInterceptor(ParamInterceptor())//添加公共请求参数
            .protocols(listOf(Protocol.HTTP_1_1)) //解决协议错误问题
        if (BaseConfig.DEBUG)
            builder.addInterceptor(okhttp3.logging.HttpLoggingInterceptor().setLevel(okhttp3.logging.HttpLoggingInterceptor.Level.BODY)) //此处设置的拦截器用来查看请求日志
        retrofit = Retrofit.Builder()
            .baseUrl(BaseConfig.getUrl())
            .client(builder.build())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> createApi(clazz: Class<T>): T {
        return retrofit!!.create(clazz)
    }

}