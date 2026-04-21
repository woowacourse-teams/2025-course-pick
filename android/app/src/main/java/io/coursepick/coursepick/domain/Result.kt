package io.coursepick.coursepick.domain

sealed interface Result<out T, out F> {
    data class Success<T>(
        val data: T,
    ) : Result<T, Nothing>

    data class Failure<F>(
        val type: F,
    ) : Result<Nothing, F>
}
