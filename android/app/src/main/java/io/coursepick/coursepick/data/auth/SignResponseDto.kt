package io.coursepick.coursepick.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignResponseDto(
    val userId: String,
    val accessToken: String,
)
