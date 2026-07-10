import UIKit

protocol MapManager {
    func drawPolyline(
        coordinates: [Coordinate],
        isSelected: Bool,
        onSelect: @escaping () -> Void
    )
    func moveCameraToContain(
        coordinates: [Coordinate],
        paddingInsets: UIEdgeInsets
    )
    func moveCamera(
        to coordinate: Coordinate,
        zoom: Double
    )
    func clearPolylines()
}
