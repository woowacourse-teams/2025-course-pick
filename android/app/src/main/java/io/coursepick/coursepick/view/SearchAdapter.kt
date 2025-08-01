package io.coursepick.coursepick.view

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import io.coursepick.coursepick.domain.SearchKeyword
import io.coursepick.coursepick.view.SearchViewHolder.Companion.SearchViewHolder

class SearchAdapter : ListAdapter<SearchKeyword, SearchViewHolder>(diffUtil) {
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
            object : DiffUtil.ItemCallback<SearchKeyword>() {
                override fun areItemsTheSame(
                    oldItem: SearchKeyword,
                    newItem: SearchKeyword,
                ): Boolean = oldItem.placeName == newItem.placeName

                override fun areContentsTheSame(
                    oldItem: SearchKeyword,
                    newItem: SearchKeyword,
                ): Boolean = oldItem == newItem
            }
    }
}
