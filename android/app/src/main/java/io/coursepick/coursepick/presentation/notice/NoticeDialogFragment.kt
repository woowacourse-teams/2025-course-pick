package io.coursepick.coursepick.presentation.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import io.coursepick.coursepick.domain.notice.Notice
import io.coursepick.coursepick.presentation.compat.getSerializableCompat
import io.coursepick.coursepick.presentation.preference.CoursePickPreferences
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

/**
 * XML Activity에서 사용할 수 있는 NoticeDialog DialogFragment
 * "다시 보지 않음" 기능을 내부적으로 처리합니다.
 *
 * 사용 예시:
 * ```
 * NoticeDialogFragment.show(
 *     fragmentManager = supportFragmentManager,
 *     notice = notice
 * )
 * ```
 */
class NoticeDialogFragment : DialogFragment() {
    private var notice: Notice? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { arguments: Bundle ->
            notice = arguments.getSerializableCompat<Notice>(ARGUMENT_NOTICE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                CoursePickTheme {
                    notice?.let { notice ->
                        NoticeDialog(
                            notice = notice,
                            onDismissRequest = ::dismiss,
                            onDoNotShowAgain = CoursePickPreferences::setDoNotShowNotice,
                        )
                    }
                }
            }
        }

    companion object {
        private const val ARGUMENT_NOTICE = "notice"

        /**
         * NoticeDialogFragment를 생성하고 표시합니다.
         *
         * @param fragmentManager FragmentManager 인스턴스
         * @param notice 표시할 공지사항 객체
         * @param tag 프래그먼트의 태그
         */
        fun show(
            fragmentManager: FragmentManager,
            notice: Notice,
            tag: String? = null,
        ) {
            val arguments =
                Bundle().apply {
                    putSerializable(ARGUMENT_NOTICE, notice)
                }

            val dialog =
                NoticeDialogFragment().apply {
                    this.arguments = arguments
                }

            dialog.show(fragmentManager, tag)
        }
    }
}
