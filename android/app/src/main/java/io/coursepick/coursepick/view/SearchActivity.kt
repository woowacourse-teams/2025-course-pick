package io.coursepick.coursepick.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.coursepick.coursepick.databinding.ActivitySearchBinding
import io.coursepick.coursepick.domain.SearchKeyword

class SearchActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySearchBinding.inflate(layoutInflater) }
    private val viewModel: SearchViewModel by viewModels()
    private val adapter: SearchAdapter by lazy { SearchAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.searchView.isIconified = false
        binding.searchView.requestFocus()
        binding.adapter = adapter

        viewModel.state.observe(this) { state: List<SearchKeyword> ->
            adapter.submitList(state)
        }

        binding.searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = true

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.search(newText.orEmpty())
                    return true
                }
            },
        )
    }

    companion object {
        fun intent(context: Context): Intent = Intent(context, SearchActivity::class.java)
    }
}
