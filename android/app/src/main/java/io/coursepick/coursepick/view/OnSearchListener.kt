package io.coursepick.coursepick.view

import io.coursepick.coursepick.domain.Place

fun interface OnSearchListener {
    fun select(place: Place)
}
