package io.coursepick.coursepick.presentation.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.data.preferences.RouteFinder
import io.coursepick.coursepick.domain.preferences.PreferencesRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PreferencesViewModel
    @Inject
    constructor(
        private val preferencesRepository: PreferencesRepository,
    ) : ViewModel() {
        private val _showRouteFinderPreferenceDialog = MutableStateFlow(false)
        val showRouteFinderPreferenceDialog: StateFlow<Boolean> get() = _showRouteFinderPreferenceDialog.asStateFlow()

        fun onOpenRouteFinderPreference() {
            _showRouteFinderPreferenceDialog.value = true
        }

        fun onSubmitRouteFinderPreference(routeFinder: RouteFinder?) {
            onDismissRouteFinderPreference()
            viewModelScope.launch {
                preferencesRepository.setRouteFinder(routeFinder)
            }
        }

        fun onDismissRouteFinderPreference() {
            _showRouteFinderPreferenceDialog.value = false
        }
    }
