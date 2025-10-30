package io.coursepick.coursepick.presentation.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

/**
 * XML Activity에서 사용할 수 있는 NoticeDialog DialogFragment
 *
 * 사용 예시:
 * ```
 * NoticeDialogFragment.show(
 *     fragmentManager = supportFragmentManager,
 *     imageUrl = "https://example.com/image.png",
 *     title = "공지사항",
 *     description = "공지사항 내용입니다.",
 *     onDoNotShowAgain = {
 *         // "다시 보지 않음" 처리 로직
 *     }
 * )
 * ```
 */
class NoticeDialogFragment : DialogFragment() {
    private var imageUrl: String = ""
    private var title: String = ""
    private var description: String = ""
    private var onDoNotShowAgain: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { arguments: Bundle ->
            imageUrl = arguments.getString(ARGUMENT_IMAGE_URL, "")
            title = arguments.getString(ARGUMENT_TITLE, "")
            description = arguments.getString(ARGUMENT_DESCRIPTION, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                CoursePickTheme {
                    NoticeDialog(
                        imageUrl = imageUrl,
                        title = title,
                        description = description,
                        onDismissRequest = { dismiss() },
                        onDoNotShowAgain = { onDoNotShowAgain?.invoke() },
                    )
                }
            }
        }

    companion object {
        private const val ARGUMENT_IMAGE_URL = "image_url"
        private const val ARGUMENT_TITLE = "title"
        private const val ARGUMENT_DESCRIPTION = "description"

        /**
         * NoticeDialogFragment를 생성하고 표시합니다.
         *
         * @param fragmentManager FragmentManager 인스턴스
         * @param imageUrl 표시할 이미지 URL
         * @param title 다이얼로그 제목
         * @param description 다이얼로그 설명
         * @param onDoNotShowAgain "다시 보지 않음" 버튼 클릭 시 호출되는 콜백
         * @param tag 프래그먼트의 태그
         */
        fun show(
            fragmentManager: FragmentManager,
            imageUrl: String,
            title: String,
            description: String,
            onDoNotShowAgain: () -> Unit,
            tag: String? = null,
        ) {
            val dialog =
                NoticeDialogFragment().apply {
                    arguments =
                        Bundle().apply {
                            putString(ARGUMENT_IMAGE_URL, imageUrl)
                            putString(ARGUMENT_TITLE, title)
                            putString(ARGUMENT_DESCRIPTION, description)
                        }
                    this.onDoNotShowAgain = onDoNotShowAgain
                }

            dialog.show(fragmentManager, tag)
        }
    }
}
