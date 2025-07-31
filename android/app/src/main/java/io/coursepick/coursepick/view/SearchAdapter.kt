package io.coursepick.coursepick.view

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import io.coursepick.coursepick.view.SearchViewHolder.Companion.SearchViewHolder

class SearchAdapter : ListAdapter<SearchKeywordItem, SearchViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SearchViewHolder = SearchViewHolder(parent)

    override fun onBindViewHolder(
        holder: SearchViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil =
            object : DiffUtil.ItemCallback<SearchKeywordItem>() {
                override fun areItemsTheSame(
                    oldItem: SearchKeywordItem,
                    newItem: SearchKeywordItem,
                ): Boolean = oldItem.keyword == newItem.keyword

                override fun areContentsTheSame(
                    oldItem: SearchKeywordItem,
                    newItem: SearchKeywordItem,
                ): Boolean = oldItem == newItem
            }
    }
}
