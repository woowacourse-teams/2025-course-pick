package io.coursepick.coursepick.data

import io.coursepick.coursepick.domain.SearchKeywordRepository
import io.coursepick.coursepick.domain.SearchKeywords

class DefaultSearchKeywordRepository : SearchKeywordRepository {
    override suspend fun searchKeywords(
        query: String,
        page: Int?,
        size: Int?,
        sort: String?,
    ): SearchKeywords =
        Services.searchKeywordService
            .searchKeywords(query, page, size, sort)
            .toSearchKeywordsOrNull() ?: SearchKeywords(emptyList())
}
