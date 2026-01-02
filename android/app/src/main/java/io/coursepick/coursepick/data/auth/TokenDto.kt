package io.coursepick.coursepick.data.auth

import kotlinx.serialization.Serializable

@Serializable
class TokenDto(
    private val accessToken: String,
)
