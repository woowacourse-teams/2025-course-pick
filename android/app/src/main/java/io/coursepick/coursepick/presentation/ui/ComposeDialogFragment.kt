package io.coursepick.coursepick.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

/**
 * Compose UI를 사용하는 DialogFragment의 베이스 클래스
 *
 * 사용 예시:
 * ```
 * class MyDialogFragment : ComposeDialogFragment() {
 *     @Composable
 *     override fun Content() {
 *         Text("Hello World")
 *     }
 * }
 * ```
 */
abstract class ComposeDialogFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                CoursePickTheme {
                    Content()
                }
            }
        }

    @Composable
    abstract fun Content()
}
