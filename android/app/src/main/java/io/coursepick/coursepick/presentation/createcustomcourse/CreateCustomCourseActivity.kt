package io.coursepick.coursepick.presentation.createcustomcourse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import io.coursepick.coursepick.databinding.ActivityCustomCourseBinding
import io.coursepick.coursepick.di.KakaoMap
import io.coursepick.coursepick.presentation.InstallStateObserver
import io.coursepick.coursepick.presentation.map.MapManager
import io.coursepick.coursepick.presentation.map.MapManagerFactory
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateCustomCourseActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCustomCourseBinding.inflate(layoutInflater) }
    private val viewModel: CreateCustomCourseViewModel by viewModels()

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
        enableEdgeToEdge()

        mapManager.startMap {
            setUpCollectors()

            if (savedInstanceState != null) {
                restoreProgress()
            }
        }

        binding.composeContainer.setContent {
            CoursePickTheme {
                CreateCustomCourseScreen(
                    length = viewModel.length.collectAsStateWithLifecycle().value,
                    onClose = ::finish,
                    onUndoWaypoint = viewModel::removeLastWaypoint,
                    onAddWaypoint = { mapManager.cameraCoordinate?.let(viewModel::addWaypoint) },
                    onConfirm = viewModel::showSubmitDialog,
                )

                if (viewModel.showSubmitDialog.collectAsStateWithLifecycle().value) {
                    SubmitCustomCourseDialog(
                        courseName = viewModel.courseName.collectAsStateWithLifecycle().value,
                        onCourseNameChange = viewModel::updateCourseName,
                        onDismiss = viewModel::dismissSubmitDialog,
                        onConfirm = viewModel::submitCourse,
                    )
                }
            }
        }
    }

    private fun setUpCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event: CreateCustomCourseUiEvent ->
                    when (event) {
                        is CreateCustomCourseUiEvent.NewSegment -> {
                            event.segment.coordinates
                                .lastOrNull()
                                ?.let(mapManager::drawWaypoint)
                            mapManager.drawDraftSegment(event.segment)
                        }

                        CreateCustomCourseUiEvent.RemoveLastWaypoint -> {
                            mapManager.removeLastWaypoint()
                        }
                    }
                }
            }
        }
    }

    private fun restoreProgress() {
        mapManager.clearWaypoints()
        mapManager.clearDraftSegments()
        viewModel.waypoints.forEach(mapManager::drawWaypoint)
        viewModel.segments.value.forEach(mapManager::drawDraftSegment)
    }

    companion object {
        fun intent(context: Context): Intent = Intent(context, CreateCustomCourseActivity::class.java)
    }
}
