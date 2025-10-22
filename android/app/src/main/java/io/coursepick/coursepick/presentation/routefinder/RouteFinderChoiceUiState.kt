package io.coursepick.coursepick.presentation.routefinder

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RouteFinderChoiceUiState(
    val routeFinders: List<RouteFinderApplication> = RouteFinderApplication.entries.toList(),
    var defaultAppChecked: Boolean = false,
) : Parcelable {
    val routeFinderNames: List<String> get() = routeFinders.map(RouteFinderApplication::appName)
}
