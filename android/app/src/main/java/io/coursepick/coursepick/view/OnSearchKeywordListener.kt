package io.coursepick.coursepick.view

import io.coursepick.coursepick.domain.SearchPlace

fun interface OnSearchKeywordListener {
    fun search(place: SearchPlace)
}
