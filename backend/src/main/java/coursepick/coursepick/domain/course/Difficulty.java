package coursepick.coursepick.domain.course;

import coursepick.coursepick.application.exception.ErrorType;

import java.util.Arrays;

public enum Difficulty {
    쉬움(1, 4, "easy"),
    보통(4, 7, "normal"),
    어려움(7, 10, "hard"),
    ;

    private final int minScore;
    private final int maxScore;
    private final String engName;

    Difficulty(int minScore, int maxScore, String engName) {
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.engName = engName;
    }

    public static Difficulty fromLengthAndRoadType(Meter length, RoadType roadType) {
        double difficultyScore = difficultyScore(length, roadType);
        if (쉬움.minScore <= difficultyScore && difficultyScore < 쉬움.maxScore) {
            return 쉬움;
        } else if (보통.minScore <= difficultyScore && difficultyScore < 보통.maxScore) {
            return 보통;
        } else {
            return 어려움;
        }
    }

    public static Difficulty fromEngName(String engName) {
        return Arrays.stream(values())
                .filter(value -> value.engName.equalsIgnoreCase(engName))
                .findAny()
                .orElseThrow(ErrorType.NOT_EXIST_DIFFICULTY::create);
    }

    private static double difficultyScore(Meter length, RoadType roadType) {
        double score = switch (roadType) {
            case RoadType.보도, RoadType.알수없음 -> 1 + (9.0 / 42195) * length.value();
            case RoadType.트랙 -> 1.0 + (9.0 / 60000) * length.value();
            case RoadType.트레일 -> 1.0 + (9.0 / 22000) * length.value();
        };

        return Math.clamp(score, 1, 10);
    }
}
