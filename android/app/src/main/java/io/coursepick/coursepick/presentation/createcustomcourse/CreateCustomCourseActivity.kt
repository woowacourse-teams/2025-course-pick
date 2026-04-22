package io.coursepick.coursepick.presentation.createcustomcourse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Latitude
import io.coursepick.coursepick.domain.course.Longitude
import io.coursepick.coursepick.presentation.InstallStateObserver
import io.coursepick.coursepick.presentation.auth.AuthDialog
import io.coursepick.coursepick.presentation.auth.AuthUiEvent
import io.coursepick.coursepick.presentation.auth.AuthViewModel
import io.coursepick.coursepick.presentation.auth.KakaoAuthenticator
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
    private val authViewModel: AuthViewModel by viewModels()

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

        setUpOnBackPressedDispatcher()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets: WindowInsetsCompat ->
            val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            mapBottomPadding = systemBars.bottom
            insets
        }

        val initialCoordinate: Coordinate? =
            intent
                .getParcelableCompat<CoordinateUiModel>(KEY_INITIAL_COORDINATE)
                ?.let { coordinate: CoordinateUiModel ->
                    Coordinate(Latitude(coordinate.latitude), Longitude(coordinate.longitude))
                }

        mapManager.startMap {
            mapManager.setPadding(bottom = mapBottomPadding)
            setUpCollectors()

            if (initialCoordinate != null) {
                mapManager.moveTo(coordinate = initialCoordinate, animate = false)
            }

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
                        isCourseNameOutOfBounds = viewModel.isCourseNameOutOfBounds.collectAsStateWithLifecycle().value,
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

                if (viewModel.showAuthDialog.collectAsStateWithLifecycle().value) {
                    AuthDialog(
                        featureName = getString(R.string.create_custom_course),
                        onDismissRequest = viewModel::dismissAuthDialog,
                        onKakaoLoginClick = { authViewModel.authenticate(KakaoAuthenticator(this@CreateCustomCourseActivity)) },
                    )
                }
            }
        }
    }

    private fun setUpOnBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.handleExitAction()
                }
            },
        )
    }

    private fun setUpCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
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

                            CreateCustomCourseUiEvent.SegmentLengthTooLong -> {
                                Toast
                                    .makeText(
                                        this@CreateCustomCourseActivity,
                                        getString(R.string.create_custom_course_waypoint_too_far_message),
                                        Toast.LENGTH_SHORT,
                                    ).show()
                            }

                            CreateCustomCourseUiEvent.CourseLengthTooLong -> {
                                Toast
                                    .makeText(
                                        this@CreateCustomCourseActivity,
                                        getString(R.string.create_custom_course_course_length_too_long_message),
                                        Toast.LENGTH_SHORT,
                                    ).show()
                            }

                            CreateCustomCourseUiEvent.Exit -> {
                                finish()
                            }

                            CreateCustomCourseUiEvent.CreateCustomCourseSuccess -> {
                                Toast
                                    .makeText(
                                        this@CreateCustomCourseActivity,
                                        getString(R.string.create_custom_course_course_added_message),
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                finish()
                            }

                            CreateCustomCourseUiEvent.CourseLengthTooShort -> {
                                Toast
                                    .makeText(
                                        this@CreateCustomCourseActivity,
                                        getString(R.string.create_custom_course_empty_course_warning),
                                        Toast.LENGTH_SHORT,
                                    ).show()
                            }

                            CreateCustomCourseUiEvent.InvalidCourseName -> {
                                Toast
                                    .makeText(
                                        this@CreateCustomCourseActivity,
                                        getString(R.string.create_custom_course_invalid_course_name_message),
                                        Toast.LENGTH_SHORT,
                                    ).show()
                            }

                            CreateCustomCourseUiEvent.DuplicateCourseName -> {
                                Toast
                                    .makeText(
                                        this@CreateCustomCourseActivity,
                                        getString(R.string.create_custom_course_duplicate_course_name_message),
                                        Toast.LENGTH_SHORT,
                                    ).show()
                            }

                            CreateCustomCourseUiEvent.UnauthorizedUser -> {
                                Toast
                                    .makeText(
                                        this@CreateCustomCourseActivity,
                                        getString(R.string.create_custom_course_unauthorized_user_message),
                                        Toast.LENGTH_SHORT,
                                    ).show()
                            }

                            CreateCustomCourseUiEvent.UnknownError -> {
                                Toast
                                    .makeText(
                                        this@CreateCustomCourseActivity,
                                        getString(R.string.create_custom_course_unknown_error_message),
                                        Toast.LENGTH_SHORT,
                                    ).show()
                            }
                        }
                    }
                }

                launch {
                    authViewModel.uiEvent.collect { event: AuthUiEvent ->
                        when (event) {
                            AuthUiEvent.AuthenticateSuccess -> {
                                viewModel.dismissAuthDialog()
                                viewModel.submitCourse()
                            }

                            AuthUiEvent.AuthenticateFailure -> {
                                Toast.makeText(this@CreateCustomCourseActivity, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
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
