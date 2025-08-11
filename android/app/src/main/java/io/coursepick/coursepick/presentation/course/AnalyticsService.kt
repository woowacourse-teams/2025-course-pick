package io.coursepick.coursepick.view

import android.os.Bundle
import io.coursepick.coursepick.view.Logger.Event

interface AnalyticsService {
    fun log(
        event: Event,
        bundle: Bundle,
    )
}
