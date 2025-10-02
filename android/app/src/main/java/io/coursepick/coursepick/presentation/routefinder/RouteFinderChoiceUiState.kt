package io.coursepick.coursepick.presentation.routefinder

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class RouteFinderChoiceUiState(
    val routeFinders: List<RouteFinderApplication> = RouteFinderApplication.ALL,
    var defaultAppChecked: Boolean = false,
) : Parcelable {
    @get:StringRes
    val routeFinderNameIds: List<Int> get() = routeFinders.map(RouteFinderApplication::nameId)
}
