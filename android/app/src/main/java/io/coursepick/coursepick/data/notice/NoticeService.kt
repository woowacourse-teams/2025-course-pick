package io.coursepick.coursepick.data.notice

import retrofit2.http.GET

interface NoticeService {
    @GET("notices")
    suspend fun notices(): List<NoticeDto>
}
