package io.coursepick.coursepick.data

import io.coursepick.coursepick.domain.SearchKeyword
import io.coursepick.coursepick.domain.SearchKeywordRepository

class DefaultSearchKeywordRepository : SearchKeywordRepository {
    override suspend fun searchKeywords(
        query: String,
        page: Int?,
        size: Int?,
        sort: String?,
    ): List<SearchKeyword> =
        Services.searchKeywordService
            .searchKeywords(query, page, size, sort)
            .toSearchKeywordsOrNull() ?: emptyList()
}
