package io.coursepick.coursepick.view

import io.coursepick.coursepick.domain.Place

fun interface OnSelectListener {
    fun select(place: Place)
}
