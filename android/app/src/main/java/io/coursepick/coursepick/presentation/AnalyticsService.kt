package io.coursepick.coursepick.presentation

import android.os.Bundle

interface AnalyticsService {
    fun log(
        event: Logger.Event,
        bundle: Bundle,
    )
}
