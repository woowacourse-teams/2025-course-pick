package io.coursepick.coursepick.presentation.search

import io.coursepick.coursepick.domain.search.Place

data class SearchUiState(
    val isQueryBlank: Boolean,
    val places: List<Place>,
    val isLoading: Boolean,
) {
    val arePlacesEmpty: Boolean get() = places.isEmpty()
}
