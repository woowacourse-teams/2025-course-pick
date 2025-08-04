package io.coursepick.coursepick.domain

data class SearchPlace(
    val id: Long,
    val addressName: String,
    val placeName: String,
    val coordinate: Coordinate,
)
