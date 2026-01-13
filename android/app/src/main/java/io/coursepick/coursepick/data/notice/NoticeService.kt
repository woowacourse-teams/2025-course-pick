package io.coursepick.coursepick.data.notice

import retrofit2.http.GET
import retrofit2.http.Path

interface NoticeService {
    @GET("notices/{id}")
    suspend fun notice(
        @Path("id") id: String,
    ): NoticeDto
}
