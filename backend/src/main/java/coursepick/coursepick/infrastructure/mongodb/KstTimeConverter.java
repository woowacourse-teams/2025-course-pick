package coursepick.coursepick.infrastructure.mongodb;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * MongoDB 저장 및 조회 시 KST(Asia/Seoul, +09:00) 타임존 보정을 처리하는 변환기입니다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KstTimeConverter {

    private static final int KST_OFFSET_HOURS = 9;

    public static Date toKstDate(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Date.from(instant.plus(KST_OFFSET_HOURS, ChronoUnit.HOURS));
    }

    public static Instant toUtcInstant(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().minus(KST_OFFSET_HOURS, ChronoUnit.HOURS);
    }
}
