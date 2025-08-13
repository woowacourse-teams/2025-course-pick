package io.coursepick.coursepick.presentation.course

import android.os.Bundle
import io.coursepick.coursepick.presentation.Logger

interface AnalyticsService {
    fun log(
        event: Logger.Event,
        bundle: Bundle,
    )
}
