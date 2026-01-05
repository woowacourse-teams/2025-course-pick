package io.coursepick.coursepick.data.search

import io.coursepick.coursepick.domain.search.Place
import io.coursepick.coursepick.domain.search.SearchRepository
import javax.inject.Inject

class DefaultSearchRepository
    @Inject
    constructor(
        private val service: SearchService,
    ) : SearchRepository {
        override suspend fun places(
            query: String,
            page: Int?,
            size: Int?,
            sort: String?,
        ): List<Place> =
            service
                .searchPlaces(query, page, size, sort)
                .toSearchPlacesOrNull() ?: emptyList()
    }
