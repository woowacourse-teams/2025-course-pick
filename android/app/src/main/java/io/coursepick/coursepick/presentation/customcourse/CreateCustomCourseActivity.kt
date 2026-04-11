package io.coursepick.coursepick.presentation.customcourse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import io.coursepick.coursepick.databinding.ActivityCustomCourseBinding
import io.coursepick.coursepick.di.KakaoMap
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.presentation.InstallStateObserver
import io.coursepick.coursepick.presentation.map.MapManager
import io.coursepick.coursepick.presentation.map.MapManagerFactory
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme
import javax.inject.Inject

@AndroidEntryPoint
class CreateCustomCourseActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCustomCourseBinding.inflate(layoutInflater) }

    @Inject
    @KakaoMap
    lateinit var mapManagerFactory: MapManagerFactory
    private val mapManager: MapManager by lazy { mapManagerFactory.create(binding.mapContainer) }

    init {
        lifecycle.addObserver(InstallStateObserver(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mapManager.startMap { }

        binding.composeContainer.setContent {
            CoursePickTheme {
                CreateCustomCourseScreen(
                    onClose = { finish() },
                    onUndoWaypoint = {
                        mapManager.removeLastWaypoint()
                    },
                    onAddWaypoint = {
                        mapManager.cameraCoordinate?.let { coordinate: Coordinate ->
                            mapManager.drawWaypoint(coordinate)
                        }
                    },
                    onConfirm = { },
                )
            }
        }
    }

    companion object {
        fun intent(context: Context): Intent = Intent(context, CreateCustomCourseActivity::class.java)
    }
}
