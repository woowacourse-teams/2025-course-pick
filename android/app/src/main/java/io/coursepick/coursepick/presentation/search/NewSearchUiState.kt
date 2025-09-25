package io.coursepick.coursepick.presentation.search

import io.coursepick.coursepick.domain.search.Place

data class NewSearchUiState(
    val isLoading: Boolean,
    val query: String,
    val places: List<Place>,
)
