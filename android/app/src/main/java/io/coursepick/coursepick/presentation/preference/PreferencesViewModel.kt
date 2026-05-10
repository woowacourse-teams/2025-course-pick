package io.coursepick.coursepick.presentation.preference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.domain.preference.PreferencesRepository
import io.coursepick.coursepick.domain.preference.RouteFinder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel
    @Inject
    constructor(
        private val preferencesRepository: PreferencesRepository,
    ) : ViewModel() {
        private val _showRouteFinderPreferenceDialog = MutableStateFlow(false)
        val showRouteFinderPreferenceDialog: StateFlow<Boolean> get() = _showRouteFinderPreferenceDialog.asStateFlow()

        val routeFinderPreference: StateFlow<RouteFinder?> =
            preferencesRepository.routeFinder.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )

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
