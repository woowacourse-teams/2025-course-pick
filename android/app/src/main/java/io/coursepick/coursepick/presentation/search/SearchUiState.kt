package io.coursepick.coursepick.presentation.search

import io.coursepick.coursepick.domain.search.Place

data class SearchUiState(
    val places: List<Place>,
    val isLoading: Boolean,
)
