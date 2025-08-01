package io.coursepick.coursepick.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.coursepick.coursepick.data.DefaultSearchKeywordRepository
import io.coursepick.coursepick.domain.SearchKeyword
import io.coursepick.coursepick.domain.SearchKeywordRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: SearchKeywordRepository = DefaultSearchKeywordRepository(),
) : ViewModel() {
    private var searchJob: Job? = null

    private val _state: MutableLiveData<List<SearchKeyword>> =
        MutableLiveData<List<SearchKeyword>>()
    val state: LiveData<List<SearchKeyword>> get() = _state

    fun search(query: String) {
        searchJob?.cancel()

        searchJob =
            viewModelScope.launch {
                delay(500)

                if (query.isNotBlank()) {
                    _state.value = searchRepository.searchKeywords(query)
                } else {
                    _state.value = emptyList()
                }
            }
    }
}
