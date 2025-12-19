package io.coursepick.coursepick.presentation.ui

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.android.material.slider.RangeSlider
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Kilometer
import io.coursepick.coursepick.domain.course.Meter
import io.coursepick.coursepick.presentation.course.CoursesUiState
import io.coursepick.coursepick.presentation.course.UiStatus
import io.coursepick.coursepick.presentation.filter.CourseFilter

@BindingAdapter("isSelected")
fun View.selected(isSelected: Boolean) {
    this.isSelected = isSelected
}

@BindingAdapter("courseLength")
fun TextView.setCourseLength(meter: Int) {
    this.text = formattedMeter(this.context, Meter(meter))
}

@BindingAdapter("courseDistance")
fun TextView.setCourseDistance(meter: Int) {
    this.text =
        this.context.getString(
            R.string.main_course_distance_suffix,
            formattedMeter(this.context, Meter(meter)),
        )
}

@BindingAdapter("swipeable")
fun DrawerLayout.setSwipeable(swipeable: Boolean) {
    val mode: Int =
        if (swipeable) {
            DrawerLayout.LOCK_MODE_UNLOCKED
        } else {
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        }
    this.setDrawerLockMode(mode)
}

@BindingAdapter("onNavigationItemSelected")
fun NavigationView.setOnNavigationItemSelected(listener: NavigationView.OnNavigationItemSelectedListener) {
    this.setNavigationItemSelectedListener(listener)
}

@BindingAdapter("simpleListItems")
fun ListView.setSimpleListItems(
    @StringRes items: List<Int>,
) {
    val adapter =
        ArrayAdapter(
            context,
            android.R.layout.simple_list_item_1,
            items.map(context::getString),
        )

    this.adapter = adapter
}

@BindingAdapter("onItemClick")
fun ListView.setOnItemClick(listener: AdapterView.OnItemClickListener) {
    this.onItemClickListener = listener
}

private fun formattedMeter(
    context: Context,
    meter: Meter,
): String =
    if (meter < Kilometer.METRIC_MULTIPLIER) {
        context.getString(R.string.main_course_unit_meter, meter.value.toInt())
    } else {
        context.getString(R.string.main_course_unit_kilometer, meter.toKilometer().value)
    }

@BindingAdapter("onNavigationClick")
fun MaterialToolbar.setOnNavigationClick(listener: View.OnClickListener) {
    setNavigationOnClickListener(listener)
}

@BindingAdapter("onRangeChanged")
fun RangeSlider.setRangeSliderListener(listener: RangeSliderListener) {
    this.addOnChangeListener { _, _, _ ->
        val values = this.values
        listener.onRangeChanged(values[0], values[1])
    }
}

fun interface RangeSliderListener {
    fun onRangeChanged(
        min: Float,
        max: Float,
    )
}

@BindingAdapter("lengthRangeText")
fun TextView.setLengthRangeText(filter: CourseFilter) {
    val start =
        filter.lengthRange.start.value
            .toInt()
    val end =
        filter.lengthRange.endInclusive.value
            .toInt()

    val min = CourseFilter.MINIMUM_LENGTH_RANGE.toInt()
    val max = CourseFilter.MAXIMUM_LENGTH_RANGE.toInt()

    val newText =
        when {
            start == min && end != max -> context.getString(R.string.length_range_open_start, end)
            start != min && end == max -> context.getString(R.string.length_range_open_end, start)
            start != min && end != max -> context.getString(R.string.length_range, start, end)
            else -> context.getString(R.string.total_length_range)
        }

    if (text.toString() != newText) {
        text = newText
    }
}

@BindingAdapter("visibleWhenNoInternet")
fun View.visibleWhenNoInternet(status: UiStatus?) {
    visibility = if (status == UiStatus.NoInternet) View.VISIBLE else View.GONE
}

@BindingAdapter("visibleWhenEmpty")
fun View.visibleWhenEmpty(state: CoursesUiState) {
    visibility =
        if (state.courses.isEmpty() && state.status == UiStatus.Success) View.VISIBLE else View.GONE
}
