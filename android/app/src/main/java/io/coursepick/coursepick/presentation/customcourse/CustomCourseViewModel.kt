package io.coursepick.coursepick.presentation.customcourse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.domain.auth.AuthRepository
import io.coursepick.coursepick.presentation.auth.AuthFeature
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomCourseViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val _uiEvent = MutableSharedFlow<CustomCourseUiEvent>()
        val uiEvent: SharedFlow<CustomCourseUiEvent> get() = _uiEvent.asSharedFlow()

        private val _authDialogState = MutableStateFlow<AuthFeature?>(null)
        val authDialogState: StateFlow<AuthFeature?> get() = _authDialogState.asStateFlow()

        fun onGoToCreateCustomCourse() {
            viewModelScope.launch {
                if (authRepository.accessToken() == null) {
                    _authDialogState.value = AuthFeature.CUSTOM_COURSE
                } else {
                    _uiEvent.emit(CustomCourseUiEvent.NavigateToCreateCourse)
                }
            }
        }

        fun dismissAuthDialog() {
            _authDialogState.value = null
        }
    }
