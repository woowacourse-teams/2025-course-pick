import NMapsMap
import UIKit

final class NaverMapManager: MapManager {
    private let mapView: NMFMapView
    private var polylineOverlays: [NMFPolylineOverlay] = []

    init(mapView: NMFMapView) {
        self.mapView = mapView
    }

    func drawPolyline(coordinates: [Coordinate]) {
        let points = coordinates.map { coordinate in
            NMGLatLng(
                lat: coordinate.latitude.value,
                lng: coordinate.longitude.value
            )
        }

        guard points.count >= 2,
              let polylineOverlay = NMFPolylineOverlay(points)
        else {
            return
        }

        polylineOverlay.width = 5
        polylineOverlay.color = .systemBlue
        polylineOverlay.mapView = mapView
        polylineOverlays.append(polylineOverlay)
    }

    func clearPolylines() {
        polylineOverlays.forEach { polylineOverlay in
            polylineOverlay.mapView = nil
        }
        polylineOverlays.removeAll()
    }
}
