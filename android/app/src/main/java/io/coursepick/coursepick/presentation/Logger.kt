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

    sealed class Event {
        abstract val name: String

        class Enter(
            target: String,
        ) : Event() {
            override val name: String = "${target}_enter"
        }

        class Exit(
            target: String,
        ) : Event() {
            override val name: String = "${target}_exit"
        }

        class Pause(
            target: String,
        ) : Event() {
            override val name: String = "${target}_pause"
        }

        class Resume(
            target: String,
        ) : Event() {
            override val name: String = "${target}_resume"
        }

        class Click(
            target: String,
        ) : Event() {
            override val name: String = "${target}_click"
        }

        class MapMoveStart(
            target: String,
        ) : Event() {
            override val name: String = "${target}_map_move_start"
        }

        class MapMoveEnd(
            target: String,
        ) : Event() {
            override val name: String = "${target}_map_move_end"
        }

        class Search(
            target: String,
        ) : Event() {
            override val name: String = "${target}_search"
        }

        class Success(
            target: String,
        ) : Event() {
            override val name: String = "${target}_success"
        }

        class Failure(
            target: String,
        ) : Event() {
            override val name: String = "${target}_failure"
        }

        class PreferenceChange(
            target: String,
        ) : Event() {
            override val name: String = "${target}_preference_change"
        }

        class Empty(
            target: String,
        ) : Event() {
            override val name: String = "empty_$target"
        }
    }
}
