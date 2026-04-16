package io.coursepick.coursepick.presentation.createcustomcourse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import io.coursepick.coursepick.R
import io.coursepick.coursepick.databinding.ActivityCustomCourseBinding
import io.coursepick.coursepick.di.KakaoMap
import io.coursepick.coursepick.presentation.InstallStateObserver
import io.coursepick.coursepick.presentation.compat.getParcelableCompat
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

    private var mapBottomPadding = 0

    init {
        lifecycle.addObserver(InstallStateObserver(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets: WindowInsetsCompat ->
            val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            mapBottomPadding = systemBars.bottom
            insets
        }

        mapManager.startMap {
            mapManager.setPadding(bottom = mapBottomPadding)
            setUpCollectors()
            intent
                .getParcelableCompat<CoordinateUiModel>(KEY_INITIAL_COORDINATE)
                ?.let { coordinate: CoordinateUiModel -> mapManager.moveTo(coordinate.value) }

            if (savedInstanceState != null) {
                restoreProgress()
            }
        }

        binding.composeContainer.setContent {
            CoursePickTheme {
                CreateCustomCourseScreen(
                    length = viewModel.length.collectAsStateWithLifecycle().value,
                    onClose = viewModel::handleExitAction,
                    onConfirm = viewModel::handleSubmitAction,
                    onUndoWaypoint = viewModel::removeLastWaypoint,
                    onAddWaypoint = { mapManager.cameraCoordinate?.let(viewModel::addWaypoint) },
                )

                if (viewModel.showSubmitDialog.collectAsStateWithLifecycle().value) {
                    SubmitCustomCourseDialog(
                        courseName = viewModel.courseName.collectAsStateWithLifecycle().value,
                        onCourseNameChange = viewModel::updateCourseName,
                        onDismiss = viewModel::dismissSubmitDialog,
                        onConfirm = viewModel::submitCourse,
                    )
                }

                if (viewModel.showDiscardDialog.collectAsStateWithLifecycle().value) {
                    DiscardCustomCourseDialog(
                        onDismiss = viewModel::dismissExitDialog,
                        onConfirm = ::finish,
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

                        CreateCustomCourseUiEvent.CourseTooShort -> {
                            Toast
                                .makeText(
                                    this@CreateCustomCourseActivity,
                                    getString(R.string.create_custom_course_empty_course_warning),
                                    Toast.LENGTH_SHORT,
                                ).show()
                        }

                        CreateCustomCourseUiEvent.Exit -> {
                            finish()
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
        private const val KEY_INITIAL_COORDINATE = "key_initial_coordinate"

        fun intent(
            context: Context,
            initialCoordinate: CoordinateUiModel?,
        ): Intent =
            Intent(
                context,
                CreateCustomCourseActivity::class.java,
            ).putExtra(KEY_INITIAL_COORDINATE, initialCoordinate)
    }
}
