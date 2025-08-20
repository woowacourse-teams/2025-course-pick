package io.coursepick.coursepick.presentation.fixtures

import io.coursepick.coursepick.data.NetworkMonitor

class FakeNetworkMonitor : NetworkMonitor {
    override fun isConnected(): Boolean = true
}
