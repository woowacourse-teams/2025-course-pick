import SwiftUI
import NMapsMap

struct NaverMapView: UIViewRepresentable {
    let polylines: [[Coordinate]]
    let selectedPolyline: [Coordinate]?
    let initialCoordinate: Coordinate?
    let bottomContentInset: CGFloat
    let onSelectPolyline: ([Coordinate]) -> Void

    func makeCoordinator() -> Coordinator {
        Coordinator()
    }

    func makeUIView(context: Context) -> NMFMapView {
        let mapView = NMFMapView()
        context.coordinator.mapManager = NaverMapManager(mapView: mapView)
        if let initialCoordinate {
            context.coordinator.mapManager?.moveCamera(
                to: initialCoordinate,
                zoom: 15
            )
        }
        return mapView
    }

    func updateUIView(_ uiView: NMFMapView, context: Context) {
        context.coordinator.mapManager?.clearPolylines()

        let selectedPolyline = selectedPolyline
        polylines
            .filter { coordinates in coordinates != selectedPolyline }
            .forEach { coordinates in
                context.coordinator.mapManager?.drawPolyline(
                    coordinates: coordinates,
                    isSelected: false
                ) {
                    onSelectPolyline(coordinates)
                }
            }

        if let selectedPolyline {
            context.coordinator.mapManager?.drawPolyline(
                coordinates: selectedPolyline,
                isSelected: true
            ) {
                onSelectPolyline(selectedPolyline)
            }
        }

        if let selectedPolyline {
            let mapManager = context.coordinator.mapManager
            let paddingInsets = UIEdgeInsets(
                top: 48,
                left: 48,
                bottom: bottomContentInset + 48,
                right: 48
            )

            DispatchQueue.main.async {
                mapManager?.moveCameraToContain(
                    coordinates: selectedPolyline,
                    paddingInsets: paddingInsets
                )
            }
        }
    }

    final class Coordinator {
        var mapManager: MapManager?
    }
}
