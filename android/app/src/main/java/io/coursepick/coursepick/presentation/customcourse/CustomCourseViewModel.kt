package io.coursepick.coursepick.presentation.customcourse

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CustomCourseViewModel
    @Inject
    constructor() : ViewModel() {
        private val _showAuthDialog = MutableStateFlow(false)
        val showAuthDialog: StateFlow<Boolean> get() = _showAuthDialog.asStateFlow()

        fun onNavigateToCreateCustomCourse() {
            _showAuthDialog.value = true
        }

        fun dismissAuthDialog() {
            _showAuthDialog.value = false
        }
    }
