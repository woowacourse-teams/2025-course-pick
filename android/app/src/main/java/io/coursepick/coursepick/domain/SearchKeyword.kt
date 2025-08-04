package io.coursepick.coursepick.domain

data class SearchKeyword(
    val id: Long,
    val addressName: String,
    val placeName: String,
    val coordinate: Coordinate,
)
