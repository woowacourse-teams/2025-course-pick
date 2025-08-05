package io.coursepick.coursepick.domain

interface SearchRepository {
    suspend fun searchPlaces(
        query: String,
        page: Int? = null,
        size: Int? = null,
        sort: String? = null,
    ): List<Place>
}
