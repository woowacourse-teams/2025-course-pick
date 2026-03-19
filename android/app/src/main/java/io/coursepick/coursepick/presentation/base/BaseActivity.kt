package io.coursepick.coursepick.presentation.base

import android.R
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus
import io.coursepick.coursepick.presentation.Logger

abstract class BaseActivity : AppCompatActivity() {
    private val updateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }

    private val onDownloadedListener =
        InstallStateUpdatedListener { state: InstallState ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                Snackbar
                    .make(
                        findViewById(R.id.content),
                        getString(io.coursepick.coursepick.R.string.app_update_downloaded_message),
                        Snackbar.LENGTH_INDEFINITE,
                    ).setAction(getString(io.coursepick.coursepick.R.string.app_update_action_after_downloaded)) {
                        updateManager.completeUpdate()
                    }.show()
            }
        }

    override fun onCreate(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?,
    ) {
        super.onCreate(savedInstanceState, persistentState)

        Logger.log(Logger.Event.Enter(javaClass.simpleName))
    }

    override fun onResume() {
        super.onResume()

        updateManager.registerListener(onDownloadedListener)
        Logger.log(Logger.Event.Resume(javaClass.simpleName))
    }

    override fun onPause() {
        Logger.log(Logger.Event.Pause(javaClass.simpleName))

        super.onPause()
    }

    override fun onStop() {
        updateManager.unregisterListener(onDownloadedListener)

        super.onStop()
    }

    override fun onDestroy() {
        Logger.log(Logger.Event.Exit(javaClass.simpleName))

        super.onDestroy()
    }
}
