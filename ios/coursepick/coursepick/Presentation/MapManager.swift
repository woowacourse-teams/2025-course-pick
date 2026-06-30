protocol MapManager {
    func drawPolyline(
        coordinates: [Coordinate],
        isSelected: Bool,
        onSelect: @escaping () -> Void
    )
    func clearPolylines()
}
