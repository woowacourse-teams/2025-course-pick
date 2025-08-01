package io.coursepick.coursepick.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.coursepick.coursepick.databinding.ItemSearchBinding
import io.coursepick.coursepick.domain.SearchKeyword

class SearchViewHolder private constructor(
    private val binding: ItemSearchBinding,
    onSearchKeywordListener: OnSearchKeywordListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onSearchKeywordListener = onSearchKeywordListener
    }

    fun bind(search: SearchKeyword) {
        binding.searchKeyword = search
    }

    companion object {
        fun SearchViewHolder(
            root: ViewGroup,
            onSearchKeywordListener: OnSearchKeywordListener,
        ): SearchViewHolder {
            val layoutInflater = LayoutInflater.from(root.context)
            val binding = ItemSearchBinding.inflate(layoutInflater, root, false)
            return SearchViewHolder(binding, onSearchKeywordListener)
        }
    }
}
