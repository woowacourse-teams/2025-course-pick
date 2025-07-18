package io.coursepick.coursepick.view

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("isSelected")
fun View.selected(isSelected: Boolean) {
    this.isSelected = isSelected
}
