package io.coursepick.coursepick.data.auth

import kotlinx.serialization.Serializable

@Serializable
class TokenDto(
    val accessToken: String,
)
