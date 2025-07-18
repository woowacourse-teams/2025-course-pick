package coursepick.coursepick.domain;

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

    public Meter minimum(Meter other) {
        return this.value() <= other.value() ? this : other;
    }
}
