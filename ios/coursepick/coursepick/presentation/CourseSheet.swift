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
    @GestureState private var dragTranslation: CGFloat = 0

    private var currentHeight: CGFloat {
        switch position {
        case .collapsed:
            collapsedHeight
        case .expanded:
            expandedHeight
        }
    }

    private var displayedHeight: CGFloat {
        min(
            max(currentHeight - dragTranslation, collapsedHeight),
            expandedHeight
        )
    }

    private var dragGesture: some Gesture {
        DragGesture()
            .updating($dragTranslation) { value, state, _ in
                state = value.translation.height
            }
            .onEnded { value in
                let predictedHeight = currentHeight - value.predictedEndTranslation.height
                let midpoint = (collapsedHeight + expandedHeight) / 2

                withAnimation(.spring(response: 0.35, dampingFraction: 0.85)) {
                    position = predictedHeight >= midpoint ? .expanded : .collapsed
                }
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
        .frame(height: displayedHeight, alignment: .top)
        .background(.backgroundPrimary)
        .clipShape(.rect(topLeadingRadius: 38, topTrailingRadius: 38))
        .overlay(alignment: .top) {
            Color.clear
                .frame(height: 44)
                .contentShape(Rectangle())
                .gesture(dragGesture)
        }
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
