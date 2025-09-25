package coursepick.coursepick.infrastructure.converter;

import coursepick.coursepick.domain.Meter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

public abstract class MeterConverter {

    @WritingConverter
    public static class Writer implements Converter<Meter, Double> {
        @Override
        public Double convert(Meter source) {
            if (source == null) return null;
            return source.value();
        }
    }

    @ReadingConverter
    public static class Reader implements Converter<Double, Meter> {
        @Override
        public Meter convert(Double source) {
            if (source == null) return null;
            return new Meter(source);
        }
    }
}
