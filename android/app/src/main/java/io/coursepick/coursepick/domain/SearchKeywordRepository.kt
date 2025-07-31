package io.coursepick.coursepick.domain

interface SearchKeywordRepository {
    suspend fun searchKeywords(
        query: String,
        page: Int? = null,
        size: Int? = null,
        sort: String? = null,
    ): SearchKeywords
}
