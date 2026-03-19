package io.coursepick.coursepick.presentation

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus
import io.coursepick.coursepick.R

abstract class BaseActivity : AppCompatActivity() {
    private val updateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }

    private val onDownloadedListener =
        InstallStateUpdatedListener { state: InstallState ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                Snackbar
                    .make(
                        findViewById(android.R.id.content),
                        getString(R.string.app_update_downloaded_message),
                        Snackbar.LENGTH_INDEFINITE,
                    ).setAction(getString(R.string.app_update_action_after_downloaded)) {
                        updateManager.completeUpdate()
                    }.show()
            }
        }

    override fun onResume() {
        super.onResume()
        updateManager.registerListener(onDownloadedListener)
    }

    override fun onStop() {
        updateManager.unregisterListener(onDownloadedListener)
        super.onStop()
    }
}
