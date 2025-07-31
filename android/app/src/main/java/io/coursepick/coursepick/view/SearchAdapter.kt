package io.coursepick.coursepick.view

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.coursepick.coursepick.view.SearchViewHolder.Companion.SearchViewHolder

class SearchAdapter(
    private val item: List<SearchItem>,
) : RecyclerView.Adapter<SearchViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SearchViewHolder = SearchViewHolder(parent)

    override fun getItemCount(): Int = item.size

    override fun onBindViewHolder(
        holder: SearchViewHolder,
        position: Int,
    ) {
        holder.bind(item[position])
    }
}
