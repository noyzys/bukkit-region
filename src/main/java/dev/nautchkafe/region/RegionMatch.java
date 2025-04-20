package dev.nautchkafe.region;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

final class RegionMatch<TYPE, RESULT> {

    private final List<Case<TYPE, RESULT>> cases;
    private Supplier<? extends RuntimeException> otherwise = () -> new IllegalArgumentException("No matching case");

    private RegionMatch() {
        this.cases = new java.util.ArrayList<>();
    }

    public static <TYPE, RESULT> RegionMatch<TYPE, RESULT> create() {
        return new RegionMatch<>();
    }

    public RegionMatch<TYPE, RESULT> when(final String pattern, final Function<TYPE, RESULT> action) {
        cases.add(new Case<>(pattern, action));
        return this;
    }

    public RegionMatch<TYPE, RESULT> otherwiseThrow(final Supplier<? extends RuntimeException> exceptionSupplier) {
        this.otherwise = exceptionSupplier;
        return this;
    }

    public Function<TYPE, RESULT> apply(final String pattern) {
        return input -> cases.stream()
            .filter(c -> c.pattern.equals(pattern))
            .findFirst()
            .map(c -> c.action.apply(input))
            .orElseThrow(otherwise);
    }

    private static final class Case<TYPE, RESULT> {
        
        final String pattern;
        final Function<TYPE, RESULT> action;

        Case(final String pattern, final Function<TYPE, RESULT> action) {
            this.pattern = pattern;
            this.action = action;
        }
    }
}