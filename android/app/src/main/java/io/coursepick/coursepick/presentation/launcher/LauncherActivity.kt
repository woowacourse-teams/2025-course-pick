package io.coursepick.coursepick.presentation.launcher

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.kakao.sdk.common.KakaoSdk
import com.kakao.vectormap.KakaoMapSdk
import dagger.hilt.android.AndroidEntryPoint
import io.coursepick.coursepick.BuildConfig
import io.coursepick.coursepick.R
import io.coursepick.coursepick.presentation.AmplitudeAnalyticsService
import io.coursepick.coursepick.presentation.AnalyticsService
import io.coursepick.coursepick.presentation.CoursePickApplication
import io.coursepick.coursepick.presentation.FirebaseAnalyticsService
import io.coursepick.coursepick.presentation.InstallationId
import io.coursepick.coursepick.presentation.Logger
import io.coursepick.coursepick.presentation.MixpanelAnalyticsService
import io.coursepick.coursepick.presentation.base.BaseActivity
import io.coursepick.coursepick.presentation.course.CoursesActivity
import io.coursepick.coursepick.presentation.preference.CoursePickPreferences
import timber.log.Timber

@AndroidEntryPoint
class LauncherActivity : BaseActivity() {
    private val installationId: InstallationId by lazy { (application as CoursePickApplication).installationId }

    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }

    private var currentUpdateType: Int = AppUpdateType.FLEXIBLE

    private val activityResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                initialize()
            } else {
                when (currentUpdateType) {
                    AppUpdateType.FLEXIBLE -> {
                        Toast
                            .makeText(
                                this,
                                R.string.app_update_cancelled_message,
                                Toast.LENGTH_SHORT,
                            ).show()

                        initialize()
                    }

                    AppUpdateType.IMMEDIATE -> {
                        Toast
                            .makeText(
                                this,
                                R.string.app_update_must_be_completed_message,
                                Toast.LENGTH_LONG,
                            ).show()

                        finish()
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition { true }
        checkForUpdate()
    }

    override fun onResume() {
        super.onResume()

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateInfo.startUpdateFlowForResult(AppUpdateType.IMMEDIATE)
            }
        }
    }

    private fun initialize() {
        val analyticsServices: List<AnalyticsService> =
            listOf(
                FirebaseAnalyticsService(installationId),
                AmplitudeAnalyticsService(applicationContext, installationId),
                MixpanelAnalyticsService(applicationContext, installationId),
            )
        Logger.init(analyticsServices)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Logger.log(Logger.Event.Enter(javaClass.simpleName))

        KakaoMapSdk.init(applicationContext, BuildConfig.KAKAO_NATIVE_APP_KEY)
        KakaoSdk.init(applicationContext, BuildConfig.KAKAO_NATIVE_APP_KEY)
        CoursePickPreferences.init(applicationContext)

        startActivity(Intent(applicationContext, CoursesActivity::class.java))
        finish()
    }

    private fun checkForUpdate() {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    appUpdateInfo.handleAvailableUpdate()
                } else {
                    initialize()
                }
            }.addOnFailureListener { initialize() }
    }

    private fun AppUpdateInfo.handleAvailableUpdate() {
        val priority = updatePriority()

        if (priority >= PRIORITY_IMMEDIATE && isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
            startUpdateFlowForResult(AppUpdateType.IMMEDIATE)
            return
        }

        if (priority >= PRIORITY_FLEXIBLE && isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
            startUpdateFlowForResult(AppUpdateType.FLEXIBLE)
            return
        }

        initialize()
    }

    private fun AppUpdateInfo.startUpdateFlowForResult(
        @AppUpdateType appUpdateType: Int,
    ) {
        currentUpdateType = appUpdateType

        appUpdateManager.startUpdateFlowForResult(
            this,
            activityResultLauncher,
            AppUpdateOptions.newBuilder(appUpdateType).build(),
        )
    }

    companion object {
        private const val PRIORITY_IMMEDIATE = 5
        private const val PRIORITY_FLEXIBLE = 3
    }
}
