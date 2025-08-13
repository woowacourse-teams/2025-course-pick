package io.coursepick.coursepick.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.coursepick.coursepick.data.search.DefaultSearchRepository
import io.coursepick.coursepick.domain.search.Place
import io.coursepick.coursepick.domain.search.SearchRepository
import io.coursepick.coursepick.presentation.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: SearchRepository = DefaultSearchRepository(),
) : ViewModel() {
    private var searchJob: Job? = null

    private val _state: MutableLiveData<List<Place>> =
        MutableLiveData<List<Place>>()
    val state: LiveData<List<Place>> get() = _state

    fun search(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            _state.value = emptyList()
            return
        }

        searchJob =
            viewModelScope.launch {
                delay(DEBOUNCE_LIMIT_TIME)
                Logger.log(Logger.Event.Search("place"), "query" to query)
                runCatching {
                    searchRepository.searchPlaces(query)
                }.onSuccess { places: List<Place> ->
                    _state.value = places
                }.onFailure {
                    _state.value = emptyList()
                }
            }
    }

    companion object {
        private const val DEBOUNCE_LIMIT_TIME = 500L
    }
}
