package io.coursepick.coursepick.view

import io.coursepick.coursepick.domain.SearchKeyword

fun interface OnSearchKeywordListener {
    fun search(place: SearchKeyword)
}
