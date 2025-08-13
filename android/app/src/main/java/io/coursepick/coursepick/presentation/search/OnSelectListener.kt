package io.coursepick.coursepick.presentation.search

import io.coursepick.coursepick.domain.search.Place

fun interface OnSelectListener {
    fun select(place: Place)
}
