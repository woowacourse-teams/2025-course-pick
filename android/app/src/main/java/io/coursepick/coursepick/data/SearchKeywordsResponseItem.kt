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
    @SerialName("meta")
    val meta: Meta?,
) {
    @Serializable
    data class Document(
        @SerialName("address_name")
        val addressName: String?,
        @SerialName("category_group_code")
        val categoryGroupCode: String?,
        @SerialName("category_group_name")
        val categoryGroupName: String?,
        @SerialName("category_name")
        val categoryName: String?,
        @SerialName("distance")
        val distance: String?,
        @SerialName("id")
        val id: String?,
        @SerialName("phone")
        val phone: String?,
        @SerialName("place_name")
        val placeName: String?,
        @SerialName("place_url")
        val placeUrl: String?,
        @SerialName("road_address_name")
        val roadAddressName: String?,
        @SerialName("x")
        val x: String?,
        @SerialName("y")
        val y: String?,
    )

    @Serializable
    data class Meta(
        @SerialName("is_end")
        val isEnd: Boolean?,
        @SerialName("pageable_count")
        val pageableCount: Int?,
        @SerialName("same_name")
        val sameName: SameName?,
        @SerialName("total_count")
        val totalCount: Int?,
    )

    @Serializable
    data class SameName(
        @SerialName("keyword")
        val keyword: String?,
        @SerialName("region")
        val region: List<String?>?,
        @SerialName("selected_region")
        val selectedRegion: String?,
    )

    fun toSearchKeywordsOrNull(): List<SearchKeyword>? = documents?.mapNotNull { it?.toSearchKeywordOrNull() }

    private fun Document.toSearchKeywordOrNull(): SearchKeyword? {
        val address = addressName ?: return null
        val place = placeName ?: return null
        val coordinate = toCoordinateOrNull(x, y) ?: return null
        return SearchKeyword(address, place, coordinate)
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
