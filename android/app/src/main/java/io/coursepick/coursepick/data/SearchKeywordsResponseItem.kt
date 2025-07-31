package io.coursepick.coursepick.data

import io.coursepick.coursepick.domain.Coordinate
import io.coursepick.coursepick.domain.Latitude
import io.coursepick.coursepick.domain.Longitude
import io.coursepick.coursepick.domain.SearchKeyword
import io.coursepick.coursepick.domain.SearchKeywords
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

    fun toSearchKeywordsOrNull(): SearchKeywords? {
        val searchKeywords: List<SearchKeyword> =
            documents?.mapNotNull { document ->
                val addressName = document?.addressName ?: return@mapNotNull null
                val placeName = document.placeName ?: return@mapNotNull null
                val lat = document.y?.toDoubleOrNull() ?: return@mapNotNull null
                val lng = document.x?.toDoubleOrNull() ?: return@mapNotNull null

                val latitude = runCatching { Latitude(lat) }.getOrNull() ?: return@mapNotNull null
                val longitude = runCatching { Longitude(lng) }.getOrNull() ?: return@mapNotNull null

                SearchKeyword(
                    addressName,
                    placeName,
                    Coordinate(latitude, longitude),
                )
            } ?: return null
        return SearchKeywords(searchKeywords)
    }
}
