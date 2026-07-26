package io.coursepick.coursepick.presentation.customcourse

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.coursepick.coursepick.R
import io.coursepick.coursepick.data.auth.KakaoAuthenticator
import io.coursepick.coursepick.databinding.FragmentCustomCoursesBinding
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.presentation.auth.AuthDialog
import io.coursepick.coursepick.presentation.compat.OnReconnectListener
import io.coursepick.coursepick.presentation.course.CourseUiModel
import io.coursepick.coursepick.presentation.course.CoursesActivity
import io.coursepick.coursepick.presentation.course.CoursesViewModel
import io.coursepick.coursepick.presentation.coursedetail.CourseDetailActivity
import io.coursepick.coursepick.presentation.createcustomcourse.CoordinateUiModel
import io.coursepick.coursepick.presentation.createcustomcourse.CreateCustomCourseActivity
import io.coursepick.coursepick.presentation.createcustomcourse.toUiModel
import kotlinx.coroutines.launch

class CustomCoursesFragment(
    private val onReconnectListener: OnReconnectListener,
) : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentCustomCoursesBinding? = null
    private val binding: FragmentCustomCoursesBinding get() = _binding!!

    private val coursesViewModel: CoursesViewModel by activityViewModels()
    private val customCourseViewModel: CustomCourseViewModel by activityViewModels()

    private val createCustomCourseLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) customCourseViewModel.fetchCustomCourses()
        }

    private val courseDetailLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) customCourseViewModel.fetchCustomCourses()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpCollectors()

        customCourseViewModel.fetchCustomCourses()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCustomCoursesBinding.inflate(inflater, container, false)
        binding.customCourses.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val nestedScrollInterop: NestedScrollConnection = rememberNestedScrollInteropConnection()
                val customCourseState: CustomCourseUiState = customCourseViewModel.state.collectAsStateWithLifecycle().value

                CustomCourseScreen(
                    status = customCourseState,
                    onReconnect = onReconnectListener,
                    onGoToCreateCustomCourse = customCourseViewModel::onGoToCreateCustomCourse,
                    onSelect = { customCourse: CustomCourseUiModel -> customCourseViewModel.select(customCourse) },
                    onDelete = { customCourse: CustomCourseUiModel -> customCourseViewModel.onDeleteCustomCourse(customCourse) },
                    onNavigateToCourse = { customCourse: CustomCourseUiModel ->
                        customCourseViewModel.onNavigateToCourse(customCourse) { course: CourseUiModel ->
                            (activity as? CoursesActivity)?.navigateToCourse(course)
                        }
                    },
                    onNavigateToDetail = { customCourse: CustomCourseUiModel ->
                        courseDetailLauncher.launch(CourseDetailActivity.intent(requireContext(), customCourse.course.id))
                    },
                    modifier = Modifier.nestedScroll(nestedScrollInterop),
                )

                val dialogState: CustomCourseViewModel.DialogState = customCourseViewModel.dialogState.collectAsStateWithLifecycle().value
                if (dialogState.authFeature != null) {
                    val featureName: String =
                        when (dialogState.authFeature) {
                            CustomCourseViewModel.AuthFeature.FetchCustomCourses -> {
                                getString(R.string.custom_course_feature_name)
                            }

                            is CustomCourseViewModel.AuthFeature.DeleteCustomCourse -> {
                                getString(R.string.delete_custom_course_dialog_feature_name)
                            }

                            CustomCourseViewModel.AuthFeature.CreateCustomCourse -> {
                                getString(R.string.create_custom_course_feature_name)
                            }
                        }

                    AuthDialog(
                        featureName = featureName,
                        onDismissRequest = customCourseViewModel::dismissAuthDialog,
                        onKakaoLoginClick = { customCourseViewModel.signIn(KakaoAuthenticator(requireContext()), dialogState.authFeature) },
                    )
                }

                if (dialogState.deleteCourseDialog != null) {
                    DeleteCustomCourseDialog(
                        courseName = dialogState.deleteCourseDialog.name,
                        onDismiss = customCourseViewModel::dismissDeleteCourseDialog,
                        onConfirm = { customCourseViewModel.confirmDeleteCustomCourse(dialogState.deleteCourseDialog.id) },
                    )
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setUpCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                customCourseViewModel.uiEvent.collect { event: CustomCourseUiEvent ->
                    when (event) {
                        CustomCourseUiEvent.NoNetwork -> {
                            Toast
                                .makeText(requireContext(), R.string.failure_no_network_message, Toast.LENGTH_SHORT)
                                .show()
                        }

                        CustomCourseUiEvent.UnauthorizedUser -> {
                            Toast
                                .makeText(requireContext(), R.string.failure_unauthorized_user_toast_message, Toast.LENGTH_SHORT)
                                .show()
                        }

                        CustomCourseUiEvent.UnknownFailure -> {
                            Toast
                                .makeText(requireContext(), R.string.failure_unknown_toast_message, Toast.LENGTH_SHORT)
                                .show()
                        }

                        CustomCourseUiEvent.AuthenticationSuccess -> {
                            Toast
                                .makeText(requireContext(), getString(R.string.authentication_success_message), Toast.LENGTH_SHORT)
                                .show()
                        }

                        CustomCourseUiEvent.AuthenticationCancelled -> {
                            Toast
                                .makeText(requireContext(), getString(R.string.authentication_cancelled_message), Toast.LENGTH_SHORT)
                                .show()
                        }

                        CustomCourseUiEvent.AuthenticationFailure -> {
                            Toast
                                .makeText(requireContext(), getString(R.string.authentication_failure_message), Toast.LENGTH_SHORT)
                                .show()
                        }

                        CustomCourseUiEvent.NavigateToCreateCourse -> {
                            goToCreateCustomCourse()
                        }

                        CustomCourseUiEvent.FetchCustomCourseFailure -> {
                            Toast
                                .makeText(requireContext(), R.string.custom_courses_load_failed, Toast.LENGTH_SHORT)
                                .show()
                        }

                        CustomCourseUiEvent.DeleteCourseSuccess -> {
                            Toast
                                .makeText(requireContext(), R.string.delete_custom_course_dialog_success_message, Toast.LENGTH_SHORT)
                                .show()
                        }

                        is CustomCourseUiEvent.SelectCustomCourse -> {
                            coursesViewModel.selectExternalCourse(event.customCourse.toCourseUiModel())
                        }
                    }
                }
            }
        }
    }

    private fun goToCreateCustomCourse() {
        val initialCoordinate: CoordinateUiModel? = coursesViewModel.mapCoordinate?.let(Coordinate::toUiModel)
        val intent: Intent = CreateCustomCourseActivity.intent(requireContext(), initialCoordinate)
        createCustomCourseLauncher.launch(intent)
    }
}
