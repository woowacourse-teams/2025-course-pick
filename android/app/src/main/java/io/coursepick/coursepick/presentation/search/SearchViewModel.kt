package io.coursepick.coursepick.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import dagger.hilt.android.lifecycle.HiltViewModel
import io.coursepick.coursepick.domain.search.Place
import io.coursepick.coursepick.domain.search.SearchRepository
import io.coursepick.coursepick.presentation.CoursePickApplication
import io.coursepick.coursepick.presentation.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel
    @Inject
    constructor(
        private val searchRepository: SearchRepository,
    ) : ViewModel() {
        private var searchJob: Job? = null

        private val _state: MutableLiveData<SearchUiState> =
            MutableLiveData<SearchUiState>(
                SearchUiState(
                    isLoading = false,
                    query = "",
                    places = emptyList(),
                ),
            )
        val state: LiveData<SearchUiState> get() = _state

        fun search(query: String) {
            searchJob?.cancel()

            if (query.isBlank()) {
                _state.value = state.value?.copy(places = emptyList(), query = query, isLoading = false)
                return
            }

            _state.value = state.value?.copy(places = emptyList(), query = query, isLoading = true)

            searchJob =
                viewModelScope.launch {
                    delay(DEBOUNCE_LIMIT_TIME)
                    Logger.log(Logger.Event.Search("place"), "query" to query)
                    runCatching {
                        searchRepository.places(query)
                    }.onSuccess { places: List<Place> ->
                        _state.value = state.value?.copy(places = places, isLoading = false)
                    }.onFailure {
                        _state.value = state.value?.copy(places = emptyList(), isLoading = false)
                    }
                }
        }

        companion object {
            private const val DEBOUNCE_LIMIT_TIME = 500L

            val Factory =
                object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(
                        modelClass: Class<T>,
                        extras: CreationExtras,
                    ): T {
                        val application = checkNotNull(extras[APPLICATION_KEY]) as CoursePickApplication
                        return SearchViewModel(application.searchRepository) as T
                    }
                }
        }
    }
