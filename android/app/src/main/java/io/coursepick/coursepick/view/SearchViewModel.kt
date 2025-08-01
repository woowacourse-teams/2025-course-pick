package io.coursepick.coursepick.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.coursepick.coursepick.data.DefaultSearchKeywordRepository
import io.coursepick.coursepick.domain.SearchKeywordRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: SearchKeywordRepository = DefaultSearchKeywordRepository(),
) : ViewModel() {
    private var searchJob: Job? = null

    private val _state: MutableLiveData<List<SearchKeywordItem>> =
        MutableLiveData<List<SearchKeywordItem>>()
    val state: LiveData<List<SearchKeywordItem>> get() = _state

    fun search(query: String) {
        searchJob?.cancel()

        searchJob =
            viewModelScope.launch {
                delay(2000)

                if (query.isNotBlank()) {
                    val results = searchRepository.searchKeywords(query)
                    val keywordsItem = results.keywords.map { SearchKeywordItem(it.placeName) }
                    _state.value = keywordsItem
                } else {
                    _state.value = emptyList()
                }
            }
    }
}
