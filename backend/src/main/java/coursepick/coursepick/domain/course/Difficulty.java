package coursepick.coursepick.domain.course;

public enum Difficulty {
    쉬움(1, 4),
    보통(4, 7),
    어려움(7, 10),
    ;

    private final int minScore;
    private final int maxScore;

    Difficulty(int minScore, int maxScore) {
        this.minScore = minScore;
        this.maxScore = maxScore;
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

    private static double difficultyScore(Meter length, RoadType roadType) {
        double score = switch (roadType) {
            case RoadType.보도, RoadType.알수없음 -> 1 + (9.0 / 42195) * length.value();
            case RoadType.트랙 -> 1.0 + (9.0 / 60000) * length.value();
            case RoadType.트레일 -> 1.0 + (9.0 / 22000) * length.value();
        };

        return Math.clamp(score, 1, 10);
    }
}
