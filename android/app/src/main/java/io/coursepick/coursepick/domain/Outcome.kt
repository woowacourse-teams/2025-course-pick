package io.coursepick.coursepick.domain

sealed interface Outcome<out T, out F> {
    data class Success<T>(
        val data: T,
    ) : Outcome<T, Nothing>

    data class Failure<E>(
        val type: E,
    ) : Outcome<Nothing, E>
}
