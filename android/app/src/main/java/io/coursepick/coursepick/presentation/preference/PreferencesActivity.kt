package io.coursepick.coursepick.presentation.preference

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import io.coursepick.coursepick.presentation.InstallStateObserver
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

@AndroidEntryPoint
class PreferencesActivity : AppCompatActivity() {
    private val viewModel: PreferencesViewModel by viewModels()

    init {
        lifecycle.addObserver(InstallStateObserver(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CoursePickTheme {
                Scaffold { innerPadding: PaddingValues ->
                    PreferencesScreen(
                        onOpenRouteFinderPreference = viewModel::onOpenRouteFinderPreference,
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                    )

                    if (viewModel.showRouteFinderPreferenceDialog.collectAsStateWithLifecycle().value) {
                        RouteFinderPreferenceDialog(
                            onConfirm = viewModel::onSubmitRouteFinderPreference,
                            onDismiss = viewModel::onDismissRouteFinderPreference,
                        )
                    }
                }
            }
        }
    }

    companion object {
        fun intent(context: Context): Intent = Intent(context, PreferencesActivity::class.java)
    }
}
