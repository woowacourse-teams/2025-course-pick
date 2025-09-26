package io.coursepick.coursepick.domain.search

interface SearchRepository {
    suspend fun places(
        query: String,
        page: Int? = null,
        size: Int? = null,
        sort: String? = null,
    ): List<Place>
}
