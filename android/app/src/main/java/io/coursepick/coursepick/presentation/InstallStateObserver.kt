package io.coursepick.coursepick.presentation

import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus

class InstallStateObserver(
    private val activity: Activity,
) : LifecycleEventObserver {
    private val updateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(activity) }

    private val onDownloadedListener =
        InstallStateUpdatedListener { state: InstallState ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                Snackbar
                    .make(
                        activity.findViewById(android.R.id.content),
                        activity.getString(io.coursepick.coursepick.R.string.app_update_downloaded_message),
                        Snackbar.LENGTH_INDEFINITE,
                    ).setAction(activity.getString(io.coursepick.coursepick.R.string.app_update_action_after_downloaded)) {
                        updateManager.completeUpdate()
                    }.show()
            }
        }

    override fun onStateChanged(
        source: LifecycleOwner,
        event: Lifecycle.Event,
    ) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                updateManager.registerListener(onDownloadedListener)
            }

            Lifecycle.Event.ON_STOP -> {
                updateManager.unregisterListener(onDownloadedListener)
            }

            else -> {}
        }
    }
}
