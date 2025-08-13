package io.coursepick.coursepick.data.search

import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {
    @GET("/v2/local/search/keyword.json")
    suspend fun searchPlaces(
        @Query("query") query: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("sort") sort: String? = null,
    ): SearchPlacesDto
}
