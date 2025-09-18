package io.coursepick.coursepick.presentation.ui

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.android.material.slider.RangeSlider
import io.coursepick.coursepick.R
import io.coursepick.coursepick.presentation.filter.FilterViewModel

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
fun ListView.setSimpleListItems(items: List<String>) {
    val adapter =
        ArrayAdapter(
            context,
            android.R.layout.simple_list_item_1,
            items,
        )

    this.adapter = adapter
}

@BindingAdapter("onItemClick")
fun ListView.setOnItemClick(listener: AdapterView.OnItemClickListener) {
    this.onItemClickListener = listener
}

@BindingAdapter("loading")
fun ContentLoadingProgressBar.setLoading(loading: Boolean) {
    if (loading) {
        this.show()
    } else {
        this.hide()
    }
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

@BindingAdapter("onRangeChanged")
fun setRangeSliderListener(
    slider: RangeSlider,
    listener: RangeSliderListener?,
) {
    listener?.let { l ->
        slider.addOnChangeListener { _, _, _ ->
            val values = slider.values
            l.onRangeChanged(values[0].toInt(), values[1].toInt())
        }
    }
}

fun interface RangeSliderListener {
    fun onRangeChanged(
        min: Int,
        max: Int,
    )
}

@BindingAdapter("values")
fun setSliderValues(
    rangeSlider: RangeSlider,
    values: List<Float>?,
) {
    values?.let {
        if (rangeSlider.values != it) {
            rangeSlider.values = it
        }
    }
}

@InverseBindingAdapter(attribute = "values", event = "valuesAttrChanged")
fun getSliderValues(rangeSlider: RangeSlider): List<Float> = rangeSlider.values

@BindingAdapter("valuesAttrChanged")
fun setSliderValuesListener(
    rangeSlider: RangeSlider,
    listener: InverseBindingListener?,
) {
    if (listener == null) return
    rangeSlider.addOnChangeListener { _, _, _ ->
        listener.onChange()
    }
}

@BindingAdapter("lengthRangeText")
fun setLengthRangeText(
    textView: TextView,
    values: List<Float>?,
) {
    if (values == null) return
    val min = values[0].toInt()
    val max = values[1].toInt()
    textView.text =
        when {
            min == FilterViewModel.MINIMUM_LENGTH_RANGE.toInt() && max != FilterViewModel.MAXIMUM_LENGTH_RANGE.toInt() ->
                textView.context.getString(R.string.length_range_open_start, max)

            min != FilterViewModel.MINIMUM_LENGTH_RANGE.toInt() && max == FilterViewModel.MAXIMUM_LENGTH_RANGE.toInt() ->
                textView.context.getString(R.string.length_range_open_end, min)

            min != FilterViewModel.MINIMUM_LENGTH_RANGE.toInt() && max != FilterViewModel.MAXIMUM_LENGTH_RANGE.toInt() ->
                textView.context.getString(R.string.length_range, min, max)

            else ->
                textView.context.getString(R.string.total_length_range)
        }
}
