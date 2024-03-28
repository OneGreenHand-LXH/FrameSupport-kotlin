package com.ogh.frame.bean

open class BaseBean {

   var errorCode: Int = -1
   var errorMsg: String = "Error"

    //判断数据是否为空(详情请看BaseModel)
    open fun isEmpty() = false
}