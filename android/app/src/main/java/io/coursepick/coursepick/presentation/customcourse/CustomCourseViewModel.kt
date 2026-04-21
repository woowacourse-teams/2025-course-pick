package io.coursepick.coursepick.presentation.customcourse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.domain.auth.AuthRepository
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
        private val _uiEvent = MutableSharedFlow<UiEvent>()
        val uiEvent: SharedFlow<UiEvent> get() = _uiEvent.asSharedFlow()

        private val _showAuthDialog = MutableStateFlow(false)
        val showAuthDialog: StateFlow<Boolean> get() = _showAuthDialog.asStateFlow()

        fun onGoToCreateCustomCourse() {
            viewModelScope.launch {
                if (authRepository.accessToken() == null) {
                    _showAuthDialog.value = true
                } else {
                    _uiEvent.emit(UiEvent.NavigateToCreateCourse)
                }
            }
        }

        fun dismissAuthDialog() {
            _showAuthDialog.value = false
        }
    }
