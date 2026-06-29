import SwiftUI

struct CourseSheet<Content: View>: View {
    let collapsedHeight: CGFloat
    let expandedHeight: CGFloat
    @Binding var displayedHeight: CGFloat
    @ViewBuilder let content: Content

    @State private var dragStartHeight: CGFloat?

    private var dragGesture: some Gesture {
        DragGesture(coordinateSpace: .global)
            .onChanged { value in
                let startHeight = dragStartHeight ?? displayedHeight
                dragStartHeight = startHeight
                displayedHeight = min(
                    max(startHeight - value.translation.height, collapsedHeight),
                    expandedHeight
                )
            }
            .onEnded { value in
                let startHeight = dragStartHeight ?? displayedHeight
                let predictedHeight = startHeight - value.predictedEndTranslation.height
                let midpoint = (collapsedHeight + expandedHeight) / 2

                displayedHeight = predictedHeight >= midpoint ? expandedHeight : collapsedHeight
                dragStartHeight = nil
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
        .frame(height: expandedHeight, alignment: .top)
        .frame(height: displayedHeight, alignment: .top)
        .background(.backgroundPrimary)
        .clipShape(.rect(topLeadingRadius: 38, topTrailingRadius: 38))
        .overlay(alignment: .top) {
            Color.clear
                .frame(height: 44)
                .contentShape(Rectangle())
                .gesture(dragGesture)
        }
        .onAppear {
            if displayedHeight == 0 {
                displayedHeight = expandedHeight
            }
        }
    }
}

#Preview {
    CourseSheet(
        collapsedHeight: 120,
        expandedHeight: 360,
        displayedHeight: .constant(360)
    ) {
        CourseListSheetView(state: .loaded)
    }
}
