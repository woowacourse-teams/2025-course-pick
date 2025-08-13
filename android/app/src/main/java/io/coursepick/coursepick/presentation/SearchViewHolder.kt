package io.coursepick.coursepick.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.coursepick.coursepick.databinding.ItemPlaceBinding
import io.coursepick.coursepick.domain.Place

class SearchViewHolder private constructor(
    private val binding: ItemPlaceBinding,
    onSelectListener: OnSelectListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onSearchKeywordListener = onSelectListener
    }

    fun bind(place: Place) {
        binding.place = place
    }

    companion object {
        operator fun invoke(
            root: ViewGroup,
            onSelectListener: OnSelectListener,
        ): SearchViewHolder {
            val layoutInflater = LayoutInflater.from(root.context)
            val binding = ItemPlaceBinding.inflate(layoutInflater, root, false)
            return SearchViewHolder(binding, onSelectListener)
        }
    }
}
