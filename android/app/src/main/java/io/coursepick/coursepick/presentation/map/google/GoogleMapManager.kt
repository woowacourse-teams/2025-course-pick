package io.coursepick.coursepick.presentation.map.google

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Point
import androidx.annotation.DrawableRes
import coil3.Bitmap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.StrokeStyle
import com.google.android.gms.maps.model.StyleSpan
import com.google.android.gms.maps.model.TextureStyle
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Scope
import io.coursepick.coursepick.domain.location.Location
import io.coursepick.coursepick.presentation.course.CourseItem
import io.coursepick.coursepick.presentation.map.DistanceCalculator
import io.coursepick.coursepick.presentation.map.MapManager

class GoogleMapManager(
    private val map: GoogleMap,
    private val context: Context,
) : MapManager {
    override val cameraCoordinate: Coordinate get() = map.cameraPosition.target.toCoordinate()
    override val scope: Scope?
        get() {
            map.projection.fromScreenLocation(Point(0, 0))
            val center =
                map.projection.visibleRegion.latLngBounds.center
                    .toCoordinate()
            val topLeft =
                map.projection.visibleRegion.farLeft
                    .toCoordinate()
            val distance = DistanceCalculator.distance(center, topLeft) ?: return null
            return Scope(distance)
        }

    private val fineUserLocationImage by lazy {
        scaleDrawable(R.drawable.image_current_location, 0.5F)
    }
    private val searchCoordinateImage by lazy {
        scaleDrawable(R.drawable.image_search_location, 0.5F)
    }

    private val polylines = mutableListOf<Polyline>()
    private var searchCoordinateMarker: Marker? = null
    private var fineUserLocationMarker: Marker? = null
    private var coarseUserLocationMarker: Circle? = null

    override fun startMap(onMapReady: () -> Unit) {
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.google_map_style))
        map.uiSettings.isCompassEnabled = false
        map.moveCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition
                    .builder()
                    .target(DEFAULT_LATLNG)
                    .zoom(DEFAULT_ZOOM_LEVEL)
                    .build(),
            ),
        )

        onMapReady()
    }

    private fun drawCourse(course: CourseItem) {
        val baseOptions =
            PolylineOptions()
                .add(*course.coordinates.map(Coordinate::toLatLng).toTypedArray())

        if (course.selected) {
            val selectedCourseOptions: PolylineOptions =
                baseOptions
                    .color(context.getColor(R.color.course_selected))
                    .width(context.resources.getDimension(R.dimen.selected_course_width))
                    .zIndex(SELECTED_COURSE_Z_INDEX)
            map
                .addPolyline(selectedCourseOptions)
                .also(polylines::add)

            val selectedCourseOverlayOptions: PolylineOptions =
                baseOptions
                    .width(context.resources.getDimension(R.dimen.selected_course_width) * 1.5F)
                    .addSpan(
                        StyleSpan(
                            StrokeStyle
                                .transparentColorBuilder()
                                .stamp(
                                    TextureStyle
                                        .newBuilder(BitmapDescriptorFactory.fromResource(R.drawable.image_arrow))
                                        .build(),
                                ).build(),
                        ),
                    ).zIndex(SELECTED_COURSE_Z_INDEX)
                    .clickable(true)
            map
                .addPolyline(selectedCourseOverlayOptions)
                .apply { tag = course }
                .also(polylines::add)
        } else {
            val unselectedCourseOptions: PolylineOptions =
                baseOptions
                    .color(context.getColor(R.color.course_unselected))
                    .width(context.resources.getDimension(R.dimen.unselected_course_width))
                    .zIndex(UNSELECTED_COURSE_Z_INDEX)
                    .clickable(true)
            map
                .addPolyline(unselectedCourseOptions)
                .apply { tag = course }
                .also(polylines::add)
        }
    }

    override fun draw(course: CourseItem) {
        removeAllRouteLines()
        drawCourse(course)
    }

    override fun draw(courses: List<CourseItem>) {
        removeAllRouteLines()
        courses.forEach(::drawCourse)
    }

    override fun drawRouteToCourse(
        route: List<Coordinate>,
        course: CourseItem,
    ) {
        removeAllRouteLines()
        map
            .addPolyline(
                PolylineOptions()
                    .add(*route.map(Coordinate::toLatLng).toTypedArray())
                    .width(context.resources.getDimension(R.dimen.course_route_width))
                    .color(context.getColor(R.color.course_route)),
            ).also(polylines::add)
        drawCourse(course)
    }

    override fun removeAllRouteLines() {
        polylines.forEach(Polyline::remove)
    }

    override fun drawSearchCoordinate(coordinate: Coordinate) {
        searchCoordinateMarker?.let { marker: Marker ->
            marker.position = coordinate.toLatLng()
        } ?: run {
            searchCoordinateMarker =
                map.addMarker(
                    MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(searchCoordinateImage))
                        .position(map.cameraPosition.target)
                        .anchor(0.5F, 1F),
                )
        }
    }

    override fun drawUserLocation(location: Location) {
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

        coarseUserLocationMarker?.let { marker: Circle ->
            animateLatLng(
                start = marker.center,
                end = location.coordinate.toLatLng(),
                duration = MOVE_ANIMATION_DURATION_MS,
            ) { latLng: LatLng -> marker.center = latLng }
        } ?: run {
            coarseUserLocationMarker =
                map.addCircle(
                    CircleOptions()
                        .center(location.coordinate.toLatLng())
                        .radius(location.accuracy.meter.value)
                        .fillColor(context.getColor(R.color.coarse_location_area))
                        .strokeWidth(0F),
                )
        }
    }

    override fun hideUserLocation() {
        hideFineUserLocation()
        hideCoarseUserLocation()
    }

    private fun hideFineUserLocation() {
        fineUserLocationMarker?.remove()
    }

    private fun hideCoarseUserLocation() {
        coarseUserLocationMarker?.remove()
    }

    override fun fitTo(coordinates: List<Coordinate>) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                LatLngBounds
                    .builder()
                    .apply { coordinates.map(Coordinate::toLatLng).forEach(::include) }
                    .build(),
                context.resources.getDimensionPixelSize(R.dimen.course_route_padding),
            ),
            MOVE_ANIMATION_DURATION_MS.toInt(),
            null,
        )
    }

    override fun fitTo(course: CourseItem) {
        fitTo(course.coordinates)
    }

    override fun setOnCourseClickListener(onClick: (CourseItem) -> Unit) {
        map.setOnPolylineClickListener { polyline: Polyline ->
            (polyline.tag as? CourseItem)?.let(onClick)
        }
    }

    override fun setOnCameraMoveListener(onCameraMove: () -> Unit) {
        map.setOnCameraMoveListener { onCameraMove() }
    }

    override fun moveTo(coordinate: Coordinate) {
        map.animateCamera(
            CameraUpdateFactory.newLatLng(coordinate.toLatLng()),
            MOVE_ANIMATION_DURATION_MS.toInt(),
            null,
        )
    }

    override fun resetZoom() {
        map.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM_LEVEL))
    }

    override fun setPadding(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
        map.setPadding(left, top, right, bottom)
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
        val original = BitmapFactory.decodeResource(context.resources, id)
        return Bitmap.createScaledBitmap(
            original,
            (original.width * scale).toInt(),
            (original.height * scale).toInt(),
            true,
        )
    }

    companion object {
        private const val MOVE_ANIMATION_DURATION_MS = 750L
        private const val DEFAULT_LATITUDE = 37.5100226
        private const val DEFAULT_LONGITUDE = 127.1026170
        private val DEFAULT_LATLNG = LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
        private const val DEFAULT_ZOOM_LEVEL = 15F
        private const val SELECTED_COURSE_Z_INDEX = 1F
        private const val UNSELECTED_COURSE_Z_INDEX = 0F
    }
}
