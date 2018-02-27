package com.jaumard.instagrim.network.utils

import android.content.Context
import android.net.ConnectivityManager
import javax.inject.Inject

class ConnectionUtils @Inject constructor(private val context: Context) {
    fun hasConnexion(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
}