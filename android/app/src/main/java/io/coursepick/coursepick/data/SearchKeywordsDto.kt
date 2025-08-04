package io.coursepick.coursepick.data

import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Latitude
import io.coursepick.coursepick.domain.Longitude
import io.coursepick.coursepick.domain.SearchKeyword
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchKeywordsDto(
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

    fun toSearchKeywordsOrNull(): List<SearchKeyword>? = documents?.mapNotNull { document: Document? -> document?.toSearchKeywordOrNull() }

    private fun Document.toSearchKeywordOrNull(): SearchKeyword? {
        val id = this.id?.toLongOrNull() ?: return null
        val addressName = this.addressName ?: return null
        val placeName = this.placeName ?: return null
        val coordinate = coordinateOrNull(x, y) ?: return null
        return SearchKeyword(id, addressName, placeName, coordinate)
    }

    private fun coordinateOrNull(
        x: String?,
        y: String?,
    ): Coordinate? {
        val latitudeValue = y?.toDoubleOrNull() ?: return null
        val longitudeValue = x?.toDoubleOrNull() ?: return null
        val latitude = runCatching { Latitude(latitudeValue) }.getOrNull() ?: return null
        val longitude = runCatching { Longitude(longitudeValue) }.getOrNull() ?: return null
        return Coordinate(latitude, longitude)
    }
}
