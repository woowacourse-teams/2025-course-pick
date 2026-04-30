package io.coursepick.coursepick.presentation.preference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.data.preference.RouteFinder
import io.coursepick.coursepick.domain.preference.SettingsRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PreferencesViewModel
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
    ) : ViewModel() {
        private val _showRouteFinderPreferenceDialog = MutableStateFlow(false)
        val showRouteFinderPreferenceDialog: StateFlow<Boolean> get() = _showRouteFinderPreferenceDialog.asStateFlow()

        fun onOpenRouteFinderPreference() {
            _showRouteFinderPreferenceDialog.value = true
        }

        fun onSubmitRouteFinderPreference(routeFinder: RouteFinder?) {
            onDismissRouteFinderPreference()
            viewModelScope.launch {
                settingsRepository.setRouteFinder(routeFinder)
            }
        }

        fun onDismissRouteFinderPreference() {
            _showRouteFinderPreferenceDialog.value = false
        }
    }
