package io.coursepick.coursepick.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.coursepick.coursepick.databinding.ItemSearchBinding

class SearchViewHolder private constructor(
    private val binding: ItemSearchBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(search: SearchItem) {
        binding.search = search
    }

    companion object {
        fun SearchViewHolder(root: ViewGroup): SearchViewHolder {
            val layoutInflater = LayoutInflater.from(root.context)
            val binding = ItemSearchBinding.inflate(layoutInflater, root, false)
            return SearchViewHolder(binding)
        }
    }
}
