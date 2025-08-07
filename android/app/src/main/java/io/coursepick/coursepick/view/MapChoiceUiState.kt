package io.coursepick.coursepick.view

import java.io.Serializable

data class MapChoiceUiState(
    val mapApplications: List<MapApplication> = MapApplication.entries.toList(),
    var defaultAppChecked: Boolean = false,
) : Serializable {
    val mapApplicationNames: List<String> get() = mapApplications.map(MapApplication::appName)
}
