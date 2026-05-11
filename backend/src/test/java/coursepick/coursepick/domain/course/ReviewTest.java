package coursepick.coursepick.domain.course;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReviewTest {

    @Test
    void 리뷰_내용이_1자면_생성된다() {
        Review review = Review.create("nickname", "좋");

        assertThat(review.content()).isEqualTo("좋");
        assertThat(review.createdAt()).isNotNull();
    }

    @Test
    void 리뷰_내용이_500자면_생성된다() {
        String content = "가".repeat(500);

        Review review = Review.create("nickname", content);

        assertThat(review.content()).hasSize(500);
    }

    @Test
    void 리뷰_내용이_비어있으면_예외가_발생한다() {
        assertThatThrownBy(() -> Review.create("nickname", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 리뷰_내용이_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> Review.create("nickname", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 리뷰_내용이_501자이면_예외가_발생한다() {
        String content = "가".repeat(501);

        assertThatThrownBy(() -> Review.create("nickname", content))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
