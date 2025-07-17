package com.wizarpos.aidldemo.util

import android.util.Log

/**
 * 日志工具类 打印调用的类和方法具体行数
 */
class LogUtil {



    companion object{
        const val API_DEBUG = true
        const val TAG = "aidldemo"
        fun d(tag:String, message:String){
            Log.d("$TAG-$tag", getStackTrace() + addSpaceInLog(message))
        }
        fun i(tag:String, message:String){
            Log.i("$TAG-$tag", getStackTrace() + addSpaceInLog(message))
        }
        fun e(tag:String, message:String){
            Log.e("$TAG-$tag", getStackTrace() + addSpaceInLog(message))
        }

        /**
         * 使用堆栈中第三行信息为调用位置信息
         * 0-当前行
         * 1-具体输出log的方法
         * 2-掉用logUtil的类
         */
        private fun getStackTrace():String{
            val sb = StringBuffer()
            val stack = Throwable().stackTrace
            val stackTraceElement = stack[2]
            if(stack.size>3){
                val index = stackTraceElement.className.lastIndexOf(".")
                val className = stackTraceElement.className.substring(index+1)
                sb.append(className)
                sb.append("-->"+stackTraceElement.methodName)
                sb.append("("+stackTraceElement.lineNumber+")")
            }
            return sb.toString()
        }
        private fun addSpaceInLog(input: String): String {
            if(API_DEBUG){
                return  input
            }
            val builder = StringBuilder()
            for (i in input.indices) {
                builder.append(input[i])
                if ((i + 1) % 8 == 0 && i != input.lastIndex) {
                    builder.append(' ')
                }
            }
            return builder.toString()
        }
    }
}