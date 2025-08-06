package io.coursepick.coursepick.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.coursepick.coursepick.databinding.ItemPlaceBinding
import io.coursepick.coursepick.domain.Place

class SearchViewHolder private constructor(
    private val binding: ItemPlaceBinding,
    onSearchListener: OnSearchListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onSearchKeywordListener = onSearchListener
    }

    fun bind(place: Place) {
        binding.place = place
    }

    companion object {
        fun SearchViewHolder(
            root: ViewGroup,
            onSearchListener: OnSearchListener,
        ): SearchViewHolder {
            val layoutInflater = LayoutInflater.from(root.context)
            val binding = ItemPlaceBinding.inflate(layoutInflater, root, false)
            return SearchViewHolder(binding, onSearchListener)
        }
    }
}
