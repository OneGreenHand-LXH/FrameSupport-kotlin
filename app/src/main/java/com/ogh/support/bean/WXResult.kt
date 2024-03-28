package com.ogh.support.bean

/**
 * @describe 微信回调结果，一般都是这些参数
 * TODO  这里要根据后台返回的字段名和返回体，进行修改
 */
class WXResult {
    var appId: String? = null
    var partnerId: String? = null
    var prepayId: String? = null
    var packageValue: String? = null
    var nonceStr: String? = null
    var timeStamp: String? = null
    var sign: String? = null
}