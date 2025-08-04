package io.coursepick.coursepick.data

import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Latitude
import io.coursepick.coursepick.domain.Longitude
import io.coursepick.coursepick.domain.SearchKeyword
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchKeywordsResponseItem(
    @SerialName("documents")
    val documents: List<Document?>?,
) {
    @Serializable
    data class Document(
        @SerialName("address_name")
        val addressName: String?,
        @SerialName("distance")
        val distance: String?,
        @SerialName("id")
        val id: String?,
        @SerialName("place_name")
        val placeName: String?,
        @SerialName("x")
        val x: String?,
        @SerialName("y")
        val y: String?,
    )

    fun toSearchKeywordsOrNull(): List<SearchKeyword>? = documents?.mapNotNull { it?.toSearchKeywordOrNull() }

    private fun Document.toSearchKeywordOrNull(): SearchKeyword? {
        val id = this.id?.toLongOrNull() ?: return null
        val address = addressName ?: return null
        val place = placeName ?: return null
        val coordinate = toCoordinateOrNull(x, y) ?: return null
        return SearchKeyword(id, address, place, coordinate)
    }

    private fun toCoordinateOrNull(
        x: String?,
        y: String?,
    ): Coordinate? {
        val lat = y?.toDoubleOrNull() ?: return null
        val lng = x?.toDoubleOrNull() ?: return null
        val latitude = runCatching { Latitude(lat) }.getOrNull() ?: return null
        val longitude = runCatching { Longitude(lng) }.getOrNull() ?: return null
        return Coordinate(latitude, longitude)
    }
}
