package coursepick.coursepick.infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import coursepick.coursepick.domain.GeoLine;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

@Converter
public class GeoLineListConverter implements AttributeConverter<List<GeoLine>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @SneakyThrows
    public String convertToDatabaseColumn(List<GeoLine> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        return objectMapper.writeValueAsString(attribute);
    }

    @Override
    @SneakyThrows
    public List<GeoLine> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(dbData, new TypeReference<>() {
        });
    }
}
