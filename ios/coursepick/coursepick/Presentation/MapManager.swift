protocol MapManager {
    func drawPolyline(
        coordinates: [Coordinate],
        isSelected: Bool,
        onSelect: @escaping () -> Void
    )
    func moveCameraToContain(coordinates: [Coordinate])
    func clearPolylines()
}
