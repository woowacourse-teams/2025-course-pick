import SwiftUI
import NMapsMap

struct NaverMapView: UIViewRepresentable {
    private static let initialCoordinate = NMGLatLng(lat: 37.515411, lng: 127.1029607)

    let polylines: [[Coordinate]]

    func makeCoordinator() -> Coordinator {
        Coordinator()
    }

    func makeUIView(context: Context) -> NMFMapView {
        let mapView = NMFMapView()
        context.coordinator.mapManager = NaverMapManager(mapView: mapView)
        moveCamera(to: Self.initialCoordinate, on: mapView)
        return mapView
    }

    func updateUIView(_ uiView: NMFMapView, context: Context) {
        context.coordinator.mapManager?.clearPolylines()
        polylines.forEach { coordinates in
            context.coordinator.mapManager?.drawPolyline(coordinates: coordinates)
        }
    }

    private func moveCamera(to coordinate: NMGLatLng, on mapView: NMFMapView) {
        DispatchQueue.main.async {
            let cameraPosition = NMFCameraPosition(coordinate, zoom: 15)
            mapView.moveCamera(NMFCameraUpdate(position: cameraPosition))
        }
    }

    final class Coordinator {
        var mapManager: MapManager?
    }
}
