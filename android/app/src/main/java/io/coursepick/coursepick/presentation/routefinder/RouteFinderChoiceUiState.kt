package io.coursepick.coursepick.presentation.routefinder

import java.io.Serializable

data class RouteFinderChoiceUiState(
    val routeFinders: List<RouteFinderApplication> = RouteFinderApplication.entries.toList(),
    var defaultAppChecked: Boolean = false,
) : Serializable {
    val routeFinderNames: List<String> get() = routeFinders.map(RouteFinderApplication::appName)
}
