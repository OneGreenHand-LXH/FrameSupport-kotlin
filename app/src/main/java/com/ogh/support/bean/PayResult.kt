package com.ogh.support.bean

import android.text.TextUtils

/**
 * @describe 支付宝支付结果
 */
class PayResult(rawResult: Map<String, String>) {
    var resultStatus: String? = null
    var result: String? = null
    var memo: String? = null

    init {
        for (key in rawResult.keys) {
            if (TextUtils.equals(key, "resultStatus")) {
                resultStatus = rawResult[key]
            } else if (TextUtils.equals(key, "result")) {
                result = rawResult[key]
            } else if (TextUtils.equals(key, "memo"))
                memo = rawResult[key]
        }
    }

    override fun toString(): String {
        return "resultStatus={$resultStatus};memo={$memo};result={$result}"
    }
}