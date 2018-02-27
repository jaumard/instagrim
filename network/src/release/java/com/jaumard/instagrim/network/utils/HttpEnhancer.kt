package com.jaumard.instagrim.network.utils

import okhttp3.OkHttpClient
import javax.inject.Inject

class HttpEnhancer @Inject constructor() {
    fun enhance(builder: OkHttpClient.Builder): OkHttpClient.Builder {
        return builder
    }
}