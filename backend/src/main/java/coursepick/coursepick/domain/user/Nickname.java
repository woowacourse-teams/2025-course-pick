package coursepick.coursepick.domain.user;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public record Nickname(
        String value
) {

    private static final List<String> ADJECTIVES = List.of(
            "피곤한", "행복한", "용감한", "졸린", "배고픈",
            "신비로운", "명랑한", "고요한", "수줍은", "엉뚱한",
            "늠름한", "다정한", "단단한", "똑똑한", "씩씩한",
            "귀여운", "느긋한", "상냥한", "재빠른", "조용한"
    );

    private static final List<String> ANIMALS = List.of(
            "하마", "기린", "펭귄", "너구리", "코알라",
            "여우", "토끼", "다람쥐", "사슴", "고양이",
            "수달", "판다", "햄스터", "두더지", "고슴도치",
            "문어", "돌고래", "거북이", "부엉이", "곰"
    );

    public static Nickname random() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        String adjective = ADJECTIVES.get(random.nextInt(ADJECTIVES.size()));
        String animal = ANIMALS.get(random.nextInt(ANIMALS.size()));
        return new Nickname(adjective + " " + animal);
    }
}
