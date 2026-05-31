package io.coursepick.coursepick.data.notice

import retrofit2.http.GET

interface NoticeService {
    @GET("/v1/notices")
    suspend fun notices(): NoticeResponseDto
}
