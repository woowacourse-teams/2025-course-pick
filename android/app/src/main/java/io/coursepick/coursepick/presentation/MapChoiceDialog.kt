package io.coursepick.coursepick.presentation

import android.content.Context
import androidx.appcompat.app.AlertDialog
import io.coursepick.coursepick.domain.Coordinate
import kotlin.enums.EnumEntries

class MapChoiceDialog(
    private val context: Context,
) {
    fun show(
        origin: Coordinate,
        destination: Coordinate,
        destinationName: String,
    ) {
        val mapApplications: EnumEntries<MapApplication> = MapApplication.entries

        AlertDialog
            .Builder(context)
            .setTitle("길찾기 앱 선택")
            .setItems(
                mapApplications.map(MapApplication::appName).toTypedArray(),
            ) { _, which: Int ->
                val selectedMapApplication: MapApplication = mapApplications[which]

                selectedMapApplication.launch(
                    context = context,
                    origin = origin,
                    destination = destination,
                    destinationName = destinationName,
                )
            }.setNegativeButton("취소", null)
            .show()
    }
}
