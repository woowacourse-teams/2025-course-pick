package io.coursepick.coursepick.data.auth

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface SignService {
    @POST("/v1/login/{type}")
    suspend fun sign(
        @Path("type") type: String,
        @Body socialToken: TokenDto,
    ): SignResponseDto
}
