package io.coursepick.coursepick.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.coursepick.coursepick.data.DefaultSearchRepository
import io.coursepick.coursepick.domain.Place
import io.coursepick.coursepick.domain.SearchRepository
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

        searchJob =
            viewModelScope.launch {
                delay(DEBOUNCE_LIMIT_TIME)

                if (query.isNotBlank()) {
                    try {
                        Logger.log(Logger.Event.Search("place"), "query" to query)
                        _state.value = searchRepository.searchPlaces(query)
                    } catch (e: Exception) {
                        _state.value = emptyList()
                    }
                } else {
                    _state.value = emptyList()
                }
            }
    }

    companion object {
        private const val DEBOUNCE_LIMIT_TIME = 500L
    }
}
