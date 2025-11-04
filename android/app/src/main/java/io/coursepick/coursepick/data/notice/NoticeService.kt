package io.coursepick.coursepick.data.notice

import retrofit2.http.GET
import retrofit2.http.Path

interface NoticeService {
    @GET("/notice/{id}")
    suspend fun notice(
        @Path("id") id: String,
    ): NoticeDto
}
