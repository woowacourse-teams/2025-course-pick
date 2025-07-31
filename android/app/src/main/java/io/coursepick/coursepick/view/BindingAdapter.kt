package io.coursepick.coursepick.view

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import io.coursepick.coursepick.R

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
    val mode =
        if (swipeable) {
            DrawerLayout.LOCK_MODE_UNLOCKED
        } else {
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        }
    this.setDrawerLockMode(mode)
}

@BindingAdapter("onNavigationItemSelectedListener")
fun NavigationView.setOnNavigationItemSelectedListener(listener: NavigationView.OnNavigationItemSelectedListener) {
    this.setNavigationItemSelectedListener(listener)
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
