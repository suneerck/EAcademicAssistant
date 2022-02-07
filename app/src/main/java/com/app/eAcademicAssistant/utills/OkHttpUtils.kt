package com.app.eAcademicAssistant.utills

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Created by Administrator on 10/3/2017.
 */

object OkHttpUtils {

    private const val TIME_OUT = 3L
    const val STATUS_OK = 200
    const val STATUS_ERROR = 500
    const val STATUS_NOT_FOUND = 404

    private var client: OkHttpClient? = null

    fun getOkHttpClient(): OkHttpClient {
        if (client == null)
            client = OkHttpClient().newBuilder()
                .connectTimeout(TIME_OUT, TimeUnit.MINUTES)
                .writeTimeout(TIME_OUT, TimeUnit.MINUTES)
                .readTimeout(TIME_OUT, TimeUnit.MINUTES)
                .build()
        return client!!
    }

    fun cancelCalls(vararg calls: okhttp3.Call?) {
        for (call in calls) {
            cancelCall(call)
        }
    }

    private fun cancelCall(call: okhttp3.Call?) {
        call?.cancel()
    }
}
