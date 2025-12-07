package io.coursepick.coursepick.presentation.course

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.coursepick.coursepick.databinding.ItemLoadingBinding

class LoadingViewHolder private constructor(
    binding: ItemLoadingBinding,
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        operator fun invoke(root: ViewGroup): LoadingViewHolder {
            val layoutInflater = LayoutInflater.from(root.context)
            val binding = ItemLoadingBinding.inflate(layoutInflater, root, false)
            return LoadingViewHolder(binding)
        }
    }
}
