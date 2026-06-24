import SwiftUI

struct CourseExploreView: View {
    @State private var sheetHeight: CGFloat = 0

    var body: some View {
        GeometryReader { geometry in
            let collapsedHeight = geometry.size.height * 0.2
            let expandedHeight = geometry.size.height * 0.5
            let displayedSheetHeight = sheetHeight == 0 ? expandedHeight : sheetHeight

            ZStack(alignment: .bottomTrailing) {
                NaverMapView()

                CourseSheet(
                    collapsedHeight: collapsedHeight,
                    expandedHeight: expandedHeight,
                    displayedHeight: $sheetHeight
                ) {
                    CourseListSheetView()
                }

                Button {
                } label: {
                    Image(systemName: "scope")
                        .frame(width: 48, height: 48)
                        .background(.backgroundPrimary)
                        .clipShape(Circle())
                }
                .padding(.trailing, 8)
                .padding(.bottom, displayedSheetHeight + 8)
                .foregroundColor(.textPrimary)
                .buttonStyle(.plain)
            }
        }
        .ignoresSafeArea()
    }
}

#Preview {
    CourseExploreView()
}
