package io.coursepick.coursepick.data.auth

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface SignService {
    @POST("/login/{socialType}")
    suspend fun sign(
        @Path("socialType") socialType: String,
        @Body socialToken: TokenDto,
    ): TokenDto
}
