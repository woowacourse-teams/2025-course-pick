package io.coursepick.coursepick.data

import io.coursepick.coursepick.domain.SearchPlace
import io.coursepick.coursepick.domain.SearchRepository

class DefaultSearchRepository : SearchRepository {
    override suspend fun searchPlaces(
        query: String,
        page: Int?,
        size: Int?,
        sort: String?,
    ): List<SearchPlace> =
        Services.searchService
            .searchPlaces(query, page, size, sort)
            .toSearchPlacesOrNull() ?: emptyList()
}
