package io.coursepick.coursepick.presentation.search

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

class NewSearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoursePickTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding: PaddingValues ->
                    SearchScreen(modifier = Modifier.padding(innerPadding))
//                    <androidx.constraintlayout.widget.ConstraintLayout
//
//
//                    <androidx.appcompat.widget.SearchView
//
//                    <androidx.core.widget.ContentLoadingProgressBar
//
//                    <androidx.recyclerview.widget.RecyclerView
//
//                    <TextView
//                    </androidx.constraintlayout.widget.ConstraintLayout>
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        SearchBar(
            inputField = {
                TextField(
                    value = "",
                    onValueChange = {},
                )
            },
            expanded = false,
            onExpandedChange = { },
        ) {
        }

        LazyColumn {
        }
    }
}

@Composable
private fun SearchResult(modifier: Modifier = Modifier) {
    Column {
        Text("")

        Text("")
    }
}

@Preview
@Composable
private fun SearchScreenPreview() {
    SearchScreen()
}
