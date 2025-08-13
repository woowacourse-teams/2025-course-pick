package io.coursepick.coursepick.data

import io.coursepick.coursepick.domain.search.Place
import io.coursepick.coursepick.domain.search.SearchRepository

class DefaultSearchRepository : SearchRepository {
    override suspend fun searchPlaces(
        query: String,
        page: Int?,
        size: Int?,
        sort: String?,
    ): List<Place> =
        Services.searchService
            .searchPlaces(query, page, size, sort)
            .toSearchPlacesOrNull() ?: emptyList()
}
