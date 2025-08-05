package coursepick.coursepick.infrastructure.mongodb;

import coursepick.coursepick.domain.Meter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

public class MeterConverter {

    @WritingConverter
    public static class Writing implements Converter<Meter, Double> {
        @Override
        public Double convert(Meter source) {
            if (source == null) {
                return null;
            }
            return source.value();
        }
    }

    @ReadingConverter
    public static class Reading implements Converter<Double, Meter> {
        @Override
        public Meter convert(Double source) {
            if (source == null) {
                // TODO : 적절하게 핸들링할것
                return null;
            }
            return new Meter(source);
        }
    }
}
