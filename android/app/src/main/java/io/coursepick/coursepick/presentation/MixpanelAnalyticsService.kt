package io.coursepick.coursepick.presentation

import android.content.Context
import com.mixpanel.android.mpmetrics.MixpanelAPI
import io.coursepick.coursepick.BuildConfig
import org.json.JSONObject

class MixpanelAnalyticsService(
    context: Context,
    installationId: InstallationId,
) : AnalyticsService {
    private val mixpanel =
        MixpanelAPI.getInstance(context, BuildConfig.MIXPANEL_PROJECT_TOKEN, false)

    init {
        mixpanel.identify(installationId.value)
    }

    override fun log(
        event: Logger.Event,
        vararg parameters: Pair<String, Any>,
    ) {
        mixpanel.track(event.name, JSONObject(parameters.toMap()))
    }
}
