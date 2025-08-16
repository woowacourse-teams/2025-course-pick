package io.coursepick.coursepick.presentation

interface AnalyticsService {
    fun log(
        event: Logger.Event,
        vararg parameters: Pair<String, Any>,
    )
}
