package coursepick.coursepick.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(JacksonConfig.class)
class JacksonConfigTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void Instant를_직렬화하면_KST_타임존이_적용된다() throws Exception {
        Instant instant = Instant.parse("2024-05-26T05:30:00Z");

        String jsonString = objectMapper.writeValueAsString(instant);

        // +9시간
        assertThat(jsonString).isEqualTo("\"2024-05-26T14:30:00+09:00\"");
    }

    @Test
    void KST_타임존_문자열을_역직렬화하면_정확한_Instant로_변환된다() throws Exception {
        String jsonString = "\"2024-05-26T14:30:00+09:00\"";

        Instant result = objectMapper.readValue(jsonString, Instant.class);

        // - 9시간
        Instant expected = Instant.parse("2024-05-26T05:30:00Z");
        assertThat(result).isEqualTo(expected);
    }
}
