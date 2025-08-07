package io.coursepick.coursepick.view

import android.app.Activity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import io.coursepick.coursepick.R

class CoursePickUpdateManager(
    private val activity: ComponentActivity,
) {
    private val appUpdateManager: AppUpdateManager =
        AppUpdateManagerFactory.create(activity).apply { registerDownloadedListener() }

    private val activityResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            if (result.resultCode != Activity.RESULT_OK) {
                Toast
                    .makeText(
                        activity,
                        activity.getString(R.string.app_update_cancelled),
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        }

    fun checkForUpdate() {
        val appUpdateInfoTask: Task<AppUpdateInfo> = appUpdateManager.appUpdateInfo

        appUpdateInfoTask
            .addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    appUpdateInfo.handleAvailableUpdate()
                }
            }
    }

    private fun AppUpdateManager.registerDownloadedListener() {
        registerListener { state: InstallState ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                showFlexibleUpdateCompleteSnackbar()
            }
        }
    }

    private fun showFlexibleUpdateCompleteSnackbar() {
        Snackbar
            .make(
                activity.findViewById(android.R.id.content),
                activity.getString(R.string.app_update_downloaded),
                Snackbar.LENGTH_INDEFINITE,
            ).setAction(activity.getString(R.string.app_update_action_after_downloaded)) {
                appUpdateManager.completeUpdate()
            }.show()
    }

    private fun AppUpdateInfo.handleAvailableUpdate() {
        val priority = updatePriority()

        if (priority == PRIORITY_IMMEDIATE && isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
            startUpdateFlowForResult(AppUpdateType.IMMEDIATE)
        } else if (isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
            startUpdateFlowForResult(AppUpdateType.FLEXIBLE)
        }
    }

    private fun AppUpdateInfo.startUpdateFlowForResult(
        @AppUpdateType appUpdateType: Int,
    ) {
        appUpdateManager.startUpdateFlowForResult(
            this,
            activityResultLauncher,
            AppUpdateOptions.newBuilder(appUpdateType).build(),
        )
    }

    companion object {
        private const val PRIORITY_IMMEDIATE = 5
    }
}
