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
import io.coursepick.coursepick.presentation.course.CoursesUiState
import io.coursepick.coursepick.presentation.course.CoursesViewModel
import io.coursepick.coursepick.presentation.course.UiStatus
import io.coursepick.coursepick.presentation.filter.FloatRange

@BindingAdapter("isSelected")
fun View.selected(isSelected: Boolean) {
    this.isSelected = isSelected
}

@BindingAdapter("courseLength")
fun TextView.setCourseLength(meter: Int) {
    this.text = formattedMeter(this.context, meter)
}

@BindingAdapter("courseDistance")
fun TextView.setCourseDistance(meter: Int) {
    this.text =
        this.context.getString(
            R.string.main_course_distance_suffix,
            formattedMeter(this.context, meter),
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
    meter: Int,
): String =
    if (meter < 1000) {
        context.getString(R.string.main_course_unit_meter, meter)
    } else {
        context.getString(R.string.main_course_unit_kilometer, meter / 1000.0)
    }

@BindingAdapter("onNavigationClick")
fun MaterialToolbar.setOnNavigationClick(listener: View.OnClickListener) {
    setNavigationOnClickListener(listener)
}

@BindingAdapter("sliderValues")
fun RangeSlider.setSliderValues(range: FloatRange?) {
    range ?: return
    val newValues = listOf(range.first, range.last)
    if (this.values != newValues) {
        this.values = newValues
    }
}

@BindingAdapter("minSeparation")
fun RangeSlider.setMinSeparation(minSeparation: Float) {
    this.setMinSeparationValue(minSeparation)
}

@BindingAdapter("onRangeChanged")
fun RangeSlider.setRangeSliderListener(listener: RangeSliderListener?) {
    listener?.let {
        this.addOnChangeListener { _, _, _ ->
            val values = this.values
            it.onRangeChanged(values[0], values[1])
        }
    }
}

fun interface RangeSliderListener {
    fun onRangeChanged(
        min: Float,
        max: Float,
    )
}

@BindingAdapter("minValueText", "maxValueText")
fun TextView.setLengthRangeText(
    min: Float,
    max: Float,
) {
    val min = min.toInt()
    val max = max.toInt()

    val newText =
        when {
            min == CoursesViewModel.MINIMUM_LENGTH_RANGE.toInt() && max != CoursesViewModel.MAXIMUM_LENGTH_RANGE.toInt() ->
                context.getString(R.string.length_range_open_start, max)

            min != CoursesViewModel.MINIMUM_LENGTH_RANGE.toInt() && max == CoursesViewModel.MAXIMUM_LENGTH_RANGE.toInt() ->
                context.getString(R.string.length_range_open_end, min)

            min != CoursesViewModel.MINIMUM_LENGTH_RANGE.toInt() && max != CoursesViewModel.MAXIMUM_LENGTH_RANGE.toInt() ->
                context.getString(R.string.length_range, min, max)

            else ->
                context.getString(R.string.total_length_range)
        }

    if (text.toString() != newText) {
        text = newText
    }
}

@BindingAdapter("isActive")
fun TextView.setActive(isActive: Boolean) {
    this.isActivated = isActive
}

@BindingAdapter("visibleWhenNoInternet")
fun View.visibleWhenNoInternet(status: UiStatus?) {
    visibility = if (status == UiStatus.NoInternet) View.VISIBLE else View.GONE
}

@BindingAdapter("visibleWhenLoading")
fun View.visibleWhenLoading(status: UiStatus?) {
    visibility = if (status == UiStatus.Loading) View.VISIBLE else View.GONE
}

@BindingAdapter("visibleWhenSuccess")
fun View.visibleWhenSuccess(status: UiStatus?) {
    visibility =
        if (status == UiStatus.Success) View.VISIBLE else View.GONE
}

@BindingAdapter("visibleWhenEmpty")
fun View.visibleWhenEmpty(state: CoursesUiState) {
    visibility =
        if (state.courses.isEmpty() && state.status == UiStatus.Success) View.VISIBLE else View.GONE
}
