package io.coursepick.coursepick.presentation

import timber.log.Timber

object Logger {
    private var analyticsServices: List<AnalyticsService>? = null

    fun init(analyticsServices: List<AnalyticsService>) {
        this.analyticsServices = analyticsServices
    }

    fun log(
        event: Event,
        vararg parameters: Pair<String, Any>,
    ) {
        debugLog(event, *parameters)
        analyticsServices?.forEach { analyticsService: AnalyticsService ->
            analyticsService.log(event, *parameters)
        }
    }

    private fun debugLog(
        event: Event,
        vararg parameters: Pair<String, Any>,
    ) {
        Timber.d(
            buildString {
                append(event.name)
                if (parameters.isNotEmpty()) {
                    append(" (")
                    append(parameters.joinToString(", ") { (key: String, value: Any) -> "$key=$value" })
                    append(")")
                }
            },
        )
    }

    sealed class Event(
        target: String? = null,
    ) {
        val name: String =
            if (target == null) {
                javaClass.simpleName.lowercase()
            } else {
                "${target}_${javaClass.simpleName.lowercase()}"
            }

        class Enter(
            target: String,
        ) : Event(target)

        class Exit(
            target: String,
        ) : Event(target)

        class Pause(
            target: String,
        ) : Event(target)

        class Resume(
            target: String,
        ) : Event(target)

        class Click(
            target: String,
        ) : Event(target)

        class MapMoveStart(
            target: String,
        ) : Event(target)

        class MapMoveEnd(
            target: String,
        ) : Event(target)

        class Search(
            target: String,
        ) : Event(target)

        class Success(
            target: String,
        ) : Event(target)

        class Failure(
            target: String,
        ) : Event(target)

        class PreferenceChange(
            target: String,
        ) : Event(target)
    }
}
