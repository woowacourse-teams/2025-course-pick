import SwiftUI

enum CourseListState {
    case loaded
    case empty
    case networkError
}

struct CourseListSheetView: View {
    let state: CourseListState

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("러닝 코스")
                .font(.system(size: 22, weight: .bold))
                .foregroundStyle(.textPrimary)
                .padding(.horizontal, 38)
                .padding(.top, 28)
                .padding(.bottom, 17)

            content
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }

    @ViewBuilder
    private var content: some View {
        switch state {
        case .loaded:
            ScrollView {
                LazyVStack(spacing: 0) {
                    ForEach(0..<20, id: \.self) { _ in
                        CourseItemView(
                            courseName: "종로5가역-경복궁-북악산-혜화역",
                            distance: 4.56,
                            length: 7.43
                        )
                    }
                }
            }

        case .empty:
            Text("이 곳엔 코스가 없어요")
                .font(.system(size: 17, weight: .bold))
                .foregroundStyle(.textPrimary)
                .frame(maxWidth: .infinity, maxHeight: .infinity)

        case .networkError:
            VStack(spacing: 8) {
                Text("네트워크에 연결되지 않았습니다.")
                    .font(.system(size: 18, weight: .regular))
                    .foregroundStyle(.textPrimary)

                Text("설정을 확인하고 다시 시도해주세요.")
                    .font(.system(size: 18, weight: .regular))
                    .foregroundStyle(.textPrimary)
            }
            .multilineTextAlignment(.center)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }
}

#Preview("Loaded") {
    CourseListSheetView(state: .loaded)
}

#Preview("Empty") {
    CourseListSheetView(state: .empty)
}

#Preview("Network Error") {
    CourseListSheetView(state: .networkError)
}
