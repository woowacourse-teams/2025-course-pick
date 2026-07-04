import NMapsMap
import UIKit

final class NaverMapManager: MapManager {
    private let mapView: NMFMapView
    private var polylineOverlays: [NMFPolylineOverlay] = []

    init(mapView: NMFMapView) {
        self.mapView = mapView
    }

    func drawPolyline(
        coordinates: [Coordinate],
        isSelected: Bool,
        onSelect: @escaping () -> Void
    ) {
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
        polylineOverlay.color = isSelected ? .courseSelected : .courseUnselected
        polylineOverlay.touchHandler = { _ in
            onSelect()
            return true
        }
        polylineOverlay.mapView = mapView
        polylineOverlays.append(polylineOverlay)
    }

    func clearPolylines() {
        polylineOverlays.forEach { polylineOverlay in
            polylineOverlay.mapView = nil
        }
        polylineOverlays.removeAll()
    }

    func moveCameraToContain(coordinates: [Coordinate]) {
        let points = coordinates.map { coordinate in
            NMGLatLng(
                lat: coordinate.latitude.value,
                lng: coordinate.longitude.value
            )
        }

        guard let firstPoint = points.first else {
            return
        }

        let bounds = points.dropFirst().reduce(
            NMGLatLngBounds(southWest: firstPoint, northEast: firstPoint)
        ) { bounds, point in
            bounds.expand(toPoint: point)
        }

        let cameraUpdate = NMFCameraUpdate(fit: bounds, padding: 48)
        cameraUpdate.animation = .easeOut
        cameraUpdate.animationDuration = 0.35
        mapView.moveCamera(cameraUpdate)
    }
}
