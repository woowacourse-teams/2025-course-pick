package io.coursepick.coursepick.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class DefaultNetworkMonitor(
    private val context: Context,
) : NetworkMonitor {
    override fun isConnected(): Boolean {
        val connectivityManager: ConnectivityManager =
            context.getSystemService(ConnectivityManager::class.java)
                ?: return false
        val currentNetwork =
            connectivityManager.activeNetwork
                ?: return false
        val caps =
            connectivityManager.getNetworkCapabilities(currentNetwork)
                ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
