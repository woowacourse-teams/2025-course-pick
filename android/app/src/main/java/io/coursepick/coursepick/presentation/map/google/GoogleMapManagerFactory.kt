package io.coursepick.coursepick.presentation.map.google

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import io.coursepick.coursepick.R
import io.coursepick.coursepick.presentation.map.MapManager
import io.coursepick.coursepick.presentation.map.MapManagerFactory
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class GoogleMapManagerFactory
    @Inject
    constructor() : MapManagerFactory {
        override suspend fun create(container: ViewGroup): MapManager =
            suspendCancellableCoroutine { continuation: CancellableContinuation<MapManager> ->
                val view: View =
                    LayoutInflater
                        .from(container.context)
                        .inflate(R.layout.layout_google_map, container, false)
                container.addView(view)

                val activity = container.context as FragmentActivity
                val mapFragment =
                    activity.supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment

                mapFragment.getMapAsync { map: GoogleMap ->
                    continuation.resume(GoogleMapManager(map, container.context))
                }
            }
    }
