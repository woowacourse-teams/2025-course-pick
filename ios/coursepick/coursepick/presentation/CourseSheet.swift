import SwiftUI

struct CourseSheet<Content: View>: View {
    private enum Position {
        case collapsed
        case expanded
    }

    let collapsedHeight: CGFloat
    let expandedHeight: CGFloat
    @ViewBuilder let content: Content

    @State private var position: Position = .expanded

    private var currentHeight: CGFloat {
        switch position {
        case .collapsed:
            collapsedHeight
        case .expanded:
            expandedHeight
        }
    }

    var body: some View {
        VStack(spacing: 0) {
            Capsule()
                .fill(.grabber)
                .frame(width: 36, height: 5)
                .padding(.top, 5)

            content
        }
        .frame(maxWidth: .infinity)
        .frame(height: currentHeight, alignment: .top)
        .background(.backgroundPrimary)
        .clipShape(.rect(topLeadingRadius: 38, topTrailingRadius: 38))
    }
}

#Preview {
    CourseSheet(
        collapsedHeight: 120,
        expandedHeight: 360
    ) {
        CourseListSheetView()
    }
}
