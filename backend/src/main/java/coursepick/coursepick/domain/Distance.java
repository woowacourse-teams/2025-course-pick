package coursepick.coursepick.domain;

public record Distance(
        double meter
) {
    public static Distance zero() {
        return new Distance(0);
    }

    public static Distance max() {
        return new Distance(Double.MAX_VALUE);
    }

    public Distance add(Distance other) {
        return new Distance(this.meter() + other.meter());
    }

    public Distance minimum(Distance other) {
        return this.meter() <= other.meter() ? this : other;
    }
}
