package io.coursepick.coursepick.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.coursepick.coursepick.data.DefaultSearchRepository
import io.coursepick.coursepick.domain.SearchPlace
import io.coursepick.coursepick.domain.SearchRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: SearchRepository = DefaultSearchRepository(),
) : ViewModel() {
    private var searchJob: Job? = null

    private val _state: MutableLiveData<List<SearchPlace>> =
        MutableLiveData<List<SearchPlace>>()
    val state: LiveData<List<SearchPlace>> get() = _state

    fun search(query: String) {
        searchJob?.cancel()

        searchJob =
            viewModelScope.launch {
                delay(DEBOUNCE_LIMIT_TIME)

                if (query.isNotBlank()) {
                    _state.value = searchRepository.searchPlaces(query)
                } else {
                    _state.value = emptyList()
                }
            }
    }

    companion object {
        private const val DEBOUNCE_LIMIT_TIME = 500L
    }
}
