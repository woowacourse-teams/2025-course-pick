package io.coursepick.coursepick.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class DefaultNetworkMonitor(
    val context: Context,
) : NetworkMonitor {
    override fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        val currentNetwork = connectivityManager.getActiveNetwork()
        val caps = connectivityManager.getNetworkCapabilities(currentNetwork)
        return caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
