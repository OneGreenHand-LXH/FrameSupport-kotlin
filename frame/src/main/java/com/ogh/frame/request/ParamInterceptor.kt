package com.ogh.frame.request

import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer

/**
 * @describe 添加公共请求参数(拦截器)
 */
class ParamInterceptor : Interceptor {
    var mGson = Gson()

    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request? = null
        val original = chain.request()
        if (original.method == "GET") { // GET 请求
            val url = original.url.newBuilder() //请求尾部链接
                .addQueryParameter("apppwd", "d28feb4ca50b2da463331a2d32") //添加参数
                .build()
            request = original.newBuilder()
                .method(original.method, original.body)
                .url(url) //添加到请求里
                .build()
        } else if (chain.request().method == "POST") { // POST 请求
            request = chain.request()
            when (request.body) {
                is FormBody -> {
                    val bodyBuilder = FormBody.Builder()
                    var formBody = request.body as FormBody
                    //把原来的参数添加到新的构造器，（因为没找到直接添加，所以就new新的）
                    for (i in 0 until formBody.size)
                        bodyBuilder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i))
                    formBody = bodyBuilder
                        .addEncoded("apppwd", "d28feb4ca50b2da463331a2d32") //添加参数
                        .build()
                    request = request.newBuilder().post(formBody).build()
                }
                is MultipartBody -> {
                    //   request = chain.request();
                    val multipartBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
                    val oldParts = (request.body as MultipartBody).parts
                    if (oldParts.isNotEmpty())
                        for (part in oldParts) multipartBuilder.addPart(part)
                    multipartBuilder.addFormDataPart("apppwd", "d28feb4ca50b2da463331a2d32")
                    request = request.newBuilder().post(multipartBuilder.build()).build()
                }
                else -> {
                    val requestBody = request.body
                    //buffer流
                    val buffer = Buffer()
                    requestBody?.writeTo(buffer)
                    var oldParamsJson = buffer.readUtf8()
                    if (oldParamsJson == "\"\"") oldParamsJson = ""
                    val rootMap: HashMap<String, Any> = mGson.fromJson(oldParamsJson, HashMap::class.java) as HashMap<String, Any> //原始参数
                    rootMap["apppwd"] = "d28feb4ca50b2da463331a2d32" //重新组装（添加参数）
                    val newJsonParams: String = mGson.toJson(rootMap) //装换成json字符串
                    request = request.newBuilder()
                        .post(newJsonParams.toRequestBody("application/json".toMediaTypeOrNull()))
                        .build()
                }
            }
        }
        return chain.proceed(request!!)
    }
}