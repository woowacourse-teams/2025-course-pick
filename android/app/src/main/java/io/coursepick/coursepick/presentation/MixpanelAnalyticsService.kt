package io.coursepick.coursepick.presentation

import android.content.Context
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.mixpanel.android.sessionreplay.MPSessionReplay
import com.mixpanel.android.sessionreplay.models.MPSessionReplayConfig
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
        mixpanel.people.set("installation_id", installationId.value)
        MPSessionReplay.initialize(
            context,
            BuildConfig.MIXPANEL_PROJECT_TOKEN,
            mixpanel.distinctId,
            MPSessionReplayConfig(wifiOnly = false),
        )
    }

    override fun log(
        event: Logger.Event,
        vararg parameters: Pair<String, Any>,
    ) {
        mixpanel.track(event.name, JSONObject(parameters.toMap()))
    }
}
