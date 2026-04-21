package io.coursepick.coursepick.presentation.customcourse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.coursepick.coursepick.databinding.FragmentCustomCoursesBinding
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.presentation.auth.AuthDialog
import io.coursepick.coursepick.presentation.auth.AuthUiEvent
import io.coursepick.coursepick.presentation.auth.AuthViewModel
import io.coursepick.coursepick.presentation.auth.KakaoAuthenticator
import io.coursepick.coursepick.presentation.course.CoursesViewModel
import io.coursepick.coursepick.presentation.createcustomcourse.CreateCustomCourseActivity
import io.coursepick.coursepick.presentation.createcustomcourse.toUiModel
import kotlinx.coroutines.launch

class CustomCoursesFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentCustomCoursesBinding? = null
    private val binding: FragmentCustomCoursesBinding get() = _binding!!

    private val coursesViewModel: CoursesViewModel by activityViewModels()
    private val customCourseViewModel: CustomCourseViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpCollectors()
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
                CustomCourseScreen(onGoToCreateCustomCourse = customCourseViewModel::onGoToCreateCustomCourse)

                val showAuthDialog: Boolean = customCourseViewModel.showAuthDialog.collectAsStateWithLifecycle().value
                if (showAuthDialog) {
                    AuthDialog(
                        featureName = "코스 추가",
                        onDismissRequest = customCourseViewModel::dismissAuthDialog,
                        onKakaoLoginClick = { authViewModel.authenticate(KakaoAuthenticator(requireActivity())) },
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
                launch {
                    customCourseViewModel.uiEvent.collect { event: UiEvent ->
                        when (event) {
                            UiEvent.NavigateToCreateCourse -> goToCreateCustomCourse()
                        }
                    }
                }

                launch {
                    authViewModel.uiEvent.collect { event: AuthUiEvent ->
                        when (event) {
                            AuthUiEvent.AuthenticateSuccess -> {
                                customCourseViewModel.dismissAuthDialog()
                                goToCreateCustomCourse()
                            }

                            AuthUiEvent.AuthenticateFailure -> {
                                Toast.makeText(requireActivity(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun goToCreateCustomCourse() {
        startActivity(
            CreateCustomCourseActivity.intent(
                requireActivity(),
                coursesViewModel.mapCoordinate?.let(Coordinate::toUiModel),
            ),
        )
    }
}
