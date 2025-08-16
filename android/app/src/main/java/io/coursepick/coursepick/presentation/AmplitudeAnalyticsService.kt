package io.coursepick.coursepick.presentation

import android.content.Context
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.amplitude.android.plugins.SessionReplayPlugin
import com.amplitude.core.events.Identify
import io.coursepick.coursepick.BuildConfig

class AmplitudeAnalyticsService(
    context: Context,
    installationId: InstallationId,
) : AnalyticsService {
    private val amplitude =
        Amplitude(
            Configuration(
                apiKey = BuildConfig.AMPLITUDE_API_KEY,
                context = context,
            ),
        )

    init {
        amplitude.identify(Identify().set("installation_id", installationId.value))
        amplitude.add(SessionReplayPlugin(sampleRate = 1.0))
    }

    override fun log(
        event: Logger.Event,
        vararg parameters: Pair<String, Any>,
    ) {
        amplitude.track(event.name, parameters.toMap())
    }
}
