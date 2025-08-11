package io.coursepick.coursepick.data.search

import io.coursepick.coursepick.data.Services
import io.coursepick.coursepick.domain.search.Place
import io.coursepick.coursepick.domain.search.SearchRepository

class DefaultSearchRepository(
    private val service: SearchService,
) : SearchRepository {
    override suspend fun searchPlaces(
        query: String,
        page: Int?,
        size: Int?,
        sort: String?,
    ): List<Place> =
        service
            .searchPlaces(query, page, size, sort)
            .toSearchPlacesOrNull() ?: emptyList()
}
