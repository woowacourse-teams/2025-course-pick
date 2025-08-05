package io.coursepick.coursepick.data

import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Latitude
import io.coursepick.coursepick.domain.Longitude
import io.coursepick.coursepick.domain.Place
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchPlacesDto(
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

    fun toSearchPlacesOrNull(): List<Place>? = documents?.mapNotNull { document: Document? -> document?.toSearchPlaceOrNull() }

    private fun Document.toSearchPlaceOrNull(): Place? {
        val id = this.id?.toLongOrNull() ?: return null
        val addressName = this.addressName ?: return null
        val placeName = this.placeName ?: return null
        val coordinate = coordinateOrNull(x, y) ?: return null
        return Place(id, addressName, placeName, coordinate)
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
