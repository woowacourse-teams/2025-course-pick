package coursepick.coursepick.domain.course;

import coursepick.coursepick.application.exception.UnauthorizedException;
import coursepick.coursepick.domain.user.User;
import org.junit.jupiter.api.Test;

import static coursepick.coursepick.test_util.CourseFixture.createSimpleCourse;
import static coursepick.coursepick.test_util.UserFixture.TEST_USER;
import static coursepick.coursepick.test_util.UserFixture.TEST_USER2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReviewTest {

    @Test
    void 리뷰_내용이_1자면_생성된다() {
        var review = new Review(TEST_USER, "좋", 1);

        assertThat(review.content()).isEqualTo("좋");
        assertThat(review.createdAt()).isNotNull();
    }

    @Test
    void 리뷰_내용이_500자면_생성된다() {
        var content = "가".repeat(500);

        var review = new Review(TEST_USER, content, 1);

        assertThat(review.content()).hasSize(500);
    }

    @Test
    void 리뷰_내용이_비어있으면_예외가_발생한다() {
        assertThatThrownBy(() -> new Review(TEST_USER, "", 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 리뷰_내용이_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> new Review(TEST_USER, null, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 리뷰_내용이_501자이면_예외가_발생한다() {
        var content = "가".repeat(501);

        assertThatThrownBy(() -> new Review(TEST_USER, content, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 리뷰_별점이_1보다_작으면_예외가_발생한다() {
        assertThatThrownBy(() -> new Review(TEST_USER, "좋은 코스", 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 리뷰_별점이_5보다_크면_예외가_발생한다() {
        assertThatThrownBy(() -> new Review(TEST_USER, "좋은 코스", 6))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 다른_유저가_리뷰를_삭제하면_예외가_발생한다() {
        var course = createSimpleCourse();
        course.reviews().add(new Review(TEST_USER, "content", 5));
        var review = course.reviews().getFirst();

        assertThatThrownBy(() -> course.verifyRemovableReview(review, TEST_USER2.id()))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void 한_유저가_같은_코스에_두_번_리뷰를_남기면_예외가_발생한다() {
        var course = createSimpleCourse();
        course.reviews().add(new Review(TEST_USER, "content", 5));

        assertThatThrownBy(() -> course.verifyWriteReviewEligibility(TEST_USER))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 리뷰_신고_시_동일_유저가_중복_신고하면_예외가_발생한다() {
        var review = new Review(TEST_USER, "content", 5);

        review.addReport(TEST_USER2);

        assertThatThrownBy(() -> review.addReport(TEST_USER2))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
