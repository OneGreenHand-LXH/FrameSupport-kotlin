package com.ogh.frame.util

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import org.json.JSONObject

/**
 * description: 解析工具类
 */
object GsonUtil {

   private fun buildGson(): Gson {
       val gsonBuilder = GsonBuilder()
       gsonBuilder.registerTypeAdapter(String::class.java, STRING)
       gsonBuilder.serializeNulls()
       return gsonBuilder.create()
   }

   /**
    * Object转成String
    */
   fun getString(value: Any): String {
       return buildGson().toJson(value)
   }

   /**
    * Object转成Bean
    */
   fun <T> getBean(value: Any, clz: Class<T>): T {
       return buildGson().fromJson(value.toString(), clz)
   }

   /**
    * 指定JSONObject转成Bean
    */
   fun <T> getBean(key: String, json: JSONObject, clz: Class<T>): T {
       return buildGson().fromJson(json.getString(key), clz)
   }

   /**
    * Object转成JSONObject
    */
   fun getJSONObject(value: Any): JSONObject {
       return JSONObject(buildGson().toJson(value))
   }

   /**
    * Object转成List<T>
    */
   fun <T> getBeanList(value: Any, typeToken: TypeToken<List<T>>): List<T> {
       return buildGson().fromJson(value.toString(), typeToken.type)
   }

   /**
    * Object转成list中有map的
    */
   fun <T> getListMap(value: Any): List<Map<String, T>> {
       return buildGson().fromJson(value.toString(), object : TypeToken<List<Map<String, T>>>() {}.type)
   }

   /**
    * Object转成Map
    */
   fun <T> getMap(value: Any): Map<String, T> {
       return buildGson().fromJson(value.toString(), object : TypeToken<Map<String, T>>() {}.type)
   }

   /***
    * 获取JSON类型
    * 判断规则:判断第一个字母是否为{或[ 如果都不是则不是一个JSON格式的文本
    * @return 0不是JSON格式的字符串 2JSONObject 1JSONArray
    */
   fun getJsonType(str: String): Int {
       if (TextUtils.isEmpty(str)) return 0
       val strChar = str.substring(0, 1).toCharArray()
       return when (strChar[0]) {
           '{' -> 2
           '[' -> 1
           else -> 0
       }
   }

   /**
    * 自定义TypeAdapter ,null对象将被解析成空字符串
    */
   private val STRING: TypeAdapter<String> = object : TypeAdapter<String>() {
       override fun read(reader: JsonReader): String {
           try {
               if (reader.peek() == JsonToken.NULL) {
                   reader.nextNull()
                   return "" // 原先是返回null，这里改为返回空字符串
               }
               return reader.nextString()
           } catch (e: Exception) {
               e.printStackTrace()
           }
           return ""
       }

       override fun write(writer: JsonWriter, value: String?) {
           try {
               if (null == value) {
                   writer.value("")
                   return
               }
               writer.value(value)
           } catch (e: Exception) {
               e.printStackTrace()
           }
       }
   }
}