package io.coursepick.coursepick.data

import retrofit2.http.GET
import retrofit2.http.Query

interface SearchKeywordService {
    @GET("/v2/local/search/keyword.json")
    suspend fun searchKeywords(
        @Query("query") query: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("sort") sort: String? = null,
    ): SearchKeywordsDto
}
