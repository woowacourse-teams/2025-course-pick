package io.coursepick.coursepick.presentation.verifiedlocations

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentManager
import io.coursepick.coursepick.presentation.ui.ComposeDialogFragment

/**
 * XML Activity에서 사용할 수 있는 VerifiedLocationsDialog DialogFragment
 *
 * 사용 예시:
 * ```
 * VerifiedLocationsDialogFragment.show(
 *     fragmentManager = supportFragmentManager,
 *     imageUrl = "https://example.com/image.png",
 *     title = "강남·송파 코스는 저희가 검증했어요",
 *     description = "* 메뉴 탭에서 다시 확인할 수 있어요."
 * )
 * ```
 */
class VerifiedLocationsDialogFragment : ComposeDialogFragment() {
    private lateinit var imageUrl: String
    private lateinit var title: String
    private lateinit var description: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageUrl = arguments?.getString(ARGUMENT_IMAGE_URL) ?: return dismiss()
        title = arguments?.getString(ARGUMENT_TITLE) ?: return dismiss()
        description = arguments?.getString(ARGUMENT_DESCRIPTION) ?: return dismiss()
    }

    @Composable
    override fun Dialog() {
        VerifiedLocationsDialog(
            imageUrl = imageUrl,
            title = title,
            description = description,
            onDismissRequest = ::dismiss,
        )
    }

    companion object {
        private const val ARGUMENT_IMAGE_URL = "imageUrl"
        private const val ARGUMENT_TITLE = "title"
        private const val ARGUMENT_DESCRIPTION = "description"

        /**
         * VerifiedLocationsDialogFragment를 생성하고 표시합니다.
         *
         * @param fragmentManager FragmentManager 인스턴스
         * @param imageUrl 표시할 이미지 URL
         * @param title 다이얼로그 제목
         * @param description 다이얼로그 설명
         * @param tag 프래그먼트의 태그
         */
        fun show(
            fragmentManager: FragmentManager,
            imageUrl: String,
            title: String,
            description: String,
            tag: String? = null,
        ) {
            val arguments =
                Bundle().apply {
                    putString(ARGUMENT_IMAGE_URL, imageUrl)
                    putString(ARGUMENT_TITLE, title)
                    putString(ARGUMENT_DESCRIPTION, description)
                }

            val dialog = VerifiedLocationsDialogFragment().apply { this.arguments = arguments }

            dialog.show(fragmentManager, tag)
        }
    }
}
