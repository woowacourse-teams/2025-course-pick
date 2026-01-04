package coursepick.coursepick.domain.course;

import coursepick.coursepick.application.exception.ErrorType;

public record Meter(
        double value
) {
    public static Meter zero() {
        return new Meter(0);
    }

    public static Meter max() {
        return new Meter(Double.MAX_VALUE);
    }

    public Meter add(Meter other) {
        return new Meter(this.value() + other.value());
    }

    public Meter clamp(double min, double max) {
        return new Meter(Math.clamp(this.value(), min, max));
    }

    public boolean isWithin(Meter other) {
        return this.value() <= other.value();
    }

    public double getRateOf(Meter other) {
        if (this.value == 0) {
            throw ErrorType.INVALID_RATIO_BASE.create();
        }
        return other.value() / this.value();
    }
}
