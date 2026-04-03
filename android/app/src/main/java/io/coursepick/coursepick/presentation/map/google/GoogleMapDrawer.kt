package io.coursepick.coursepick.presentation.map.google

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.core.graphics.scale
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.StrokeStyle
import com.google.android.gms.maps.model.StyleSpan
import com.google.android.gms.maps.model.TextureStyle
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.location.Location
import io.coursepick.coursepick.presentation.course.CourseItem

class GoogleMapDrawer(
    private val map: GoogleMap,
    private val context: Context,
) {
    private val polylines = mutableListOf<Polyline>()
    private var searchCoordinateMarker: Marker? = null
    private var fineUserLocationMarker: Marker? = null
    private var coarseUserLocationCircle: Circle? = null

    private val fineUserLocationImage by lazy {
        scaleDrawable(R.drawable.image_current_location, 0.5F)
    }
    private val searchCoordinateImage by lazy {
        scaleDrawable(R.drawable.image_search_location, 0.5F)
    }

    fun drawCourse(course: CourseItem) {
        if (course.selected) {
            drawSelectedCourse(course)
        } else {
            drawUnselectedCourse(course)
        }
    }

    fun drawRouteToCourse(
        route: List<Coordinate>,
        course: CourseItem,
    ) {
        drawRoute(route)
        drawSelectedCourse(course)
    }

    private fun drawRoute(route: List<Coordinate>) {
        val options =
            PolylineOptions()
                .add(*route.map(Coordinate::toLatLng).toTypedArray())
                .width(context.resources.getDimension(R.dimen.course_route_width))
                .color(context.getColor(R.color.course_route))

        map.addPolyline(options).also(polylines::add)
    }

    private fun drawUnselectedCourse(course: CourseItem) {
        val options =
            PolylineOptions()
                .add(*course.coordinates.map(Coordinate::toLatLng).toTypedArray())
                .color(context.getColor(R.color.course_unselected))
                .width(context.resources.getDimension(R.dimen.unselected_course_width))
                .zIndex(UNSELECTED_COURSE_Z_INDEX)
                .clickable(true)

        map.addPolyline(options).apply { tag = course }.also(polylines::add)
    }

    private fun drawSelectedCourse(course: CourseItem) {
        val courseWidth: Float = context.resources.getDimension(R.dimen.selected_course_width)

        val baseOptions =
            PolylineOptions()
                .add(*course.coordinates.map(Coordinate::toLatLng).toTypedArray())

        val courseOptions: PolylineOptions =
            baseOptions
                .color(context.getColor(R.color.course_selected))
                .width(courseWidth)
                .zIndex(SELECTED_COURSE_Z_INDEX)

        map.addPolyline(courseOptions).also(polylines::add)

        val courseStrokeStyle =
            StrokeStyle
                .transparentColorBuilder()
                .stamp(
                    TextureStyle
                        .newBuilder(BitmapDescriptorFactory.fromResource(R.drawable.image_arrow))
                        .build(),
                ).build()
        val courseOverlayOptions: PolylineOptions =
            baseOptions
                .width(courseWidth * 2F)
                .addSpan(StyleSpan(courseStrokeStyle))
                .zIndex(SELECTED_COURSE_Z_INDEX)
                .clickable(true)

        map.addPolyline(courseOverlayOptions).apply { tag = course }.also(polylines::add)
    }

    fun removeAllRouteLines() {
        polylines.forEach(Polyline::remove)
        polylines.clear()
    }

    fun drawSearchCoordinate(coordinate: Coordinate) {
        searchCoordinateMarker?.let { marker: Marker ->
            marker.position = coordinate.toLatLng()
        } ?: run {
            searchCoordinateMarker =
                map.addMarker(
                    MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(searchCoordinateImage))
                        .position(coordinate.toLatLng())
                        .anchor(0.5F, 1F),
                )
        }
    }

    fun drawUserLocation(location: Location) {
        when (location) {
            is Location.Fine -> drawFineUserLocation(location)
            is Location.Coarse -> drawCoarseUserLocation(location)
        }
    }

    private fun drawFineUserLocation(location: Location.Fine) {
        hideCoarseUserLocation()

        fineUserLocationMarker?.let { marker: Marker ->
            animateLatLng(
                start = marker.position,
                end = location.coordinate.toLatLng(),
                duration = MOVE_ANIMATION_DURATION_MS,
            ) { latLng: LatLng -> marker.position = latLng }
        } ?: run {
            fineUserLocationMarker =
                map.addMarker(
                    MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(fineUserLocationImage))
                        .position(location.coordinate.toLatLng())
                        .anchor(0.5F, 0.5F),
                )
        }
    }

    private fun drawCoarseUserLocation(location: Location.Coarse) {
        hideFineUserLocation()

        coarseUserLocationCircle?.let { circle: Circle ->
            circle.radius = location.accuracy.meter.value
            animateLatLng(
                start = circle.center,
                end = location.coordinate.toLatLng(),
                duration = MOVE_ANIMATION_DURATION_MS,
            ) { latLng: LatLng -> circle.center = latLng }
        } ?: run {
            coarseUserLocationCircle =
                map.addCircle(
                    CircleOptions()
                        .center(location.coordinate.toLatLng())
                        .radius(location.accuracy.meter.value)
                        .fillColor(context.getColor(R.color.coarse_location_area))
                        .strokeWidth(0F),
                )
        }
    }

    fun hideUserLocation() {
        hideFineUserLocation()
        hideCoarseUserLocation()
    }

    private fun hideFineUserLocation() {
        fineUserLocationMarker?.remove()
        fineUserLocationMarker = null
    }

    private fun hideCoarseUserLocation() {
        coarseUserLocationCircle?.remove()
        coarseUserLocationCircle = null
    }

    private fun animateLatLng(
        start: LatLng,
        end: LatLng,
        duration: Long,
        onChange: (latLng: LatLng) -> Unit,
    ) {
        val valueAnimator = ValueAnimator.ofFloat(0F, 1F).setDuration(duration)
        valueAnimator.addUpdateListener { animator: ValueAnimator ->
            val latitude =
                (end.latitude - start.latitude) * animator.animatedFraction + start.latitude
            val longitude =
                (end.longitude - start.longitude) * animator.animatedFraction + start.longitude
            onChange(LatLng(latitude, longitude))
        }
        valueAnimator.start()
    }

    private fun scaleDrawable(
        @DrawableRes id: Int,
        scale: Float,
    ): Bitmap {
        val original: Bitmap = BitmapFactory.decodeResource(context.resources, id)
        return original.scale((original.width * scale).toInt(), (original.height * scale).toInt())
    }

    companion object {
        private const val MOVE_ANIMATION_DURATION_MS = 750L
        private const val UNSELECTED_COURSE_Z_INDEX = 0F
        private const val SELECTED_COURSE_Z_INDEX = 1F
    }
}
