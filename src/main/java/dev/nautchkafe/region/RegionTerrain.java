package dev.nautchkafe.region;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

final class RegionTerrain implements Region {

    private final RegionLocation min;
    private final RegionLocation max;
    private final Predicate<RegionLocation> containsPredicate;

    private RegionTerrain(
        final RegionLocation min, 
        final RegionLocation max, 
        final Predicate<RegionLocation> containsPredicate
    ) {
        this.min = min;
        this.max = max;
        this.containsPredicate = containsPredicate;
    }

    @Override
    public final boolean contains(final RegionLocation location) {
        return containsPredicate.test(location);
    }

    @Override
    public final Region applyToBounds(final Function<Double, Double> operation) {
        return new RegionTerrain(
            new RegionLocation(
                min.world(), 
                operation.apply(min.x()), 
                operation.apply(min.y()), 
                operation.apply(min.z())
            ),
            new RegionLocation(
                max.world(), 
                operation.apply(max.x()), 
                operation.apply(max.y()), 
                operation.apply(max.z())
            ),
            this.containsPredicate
        );
    }

    @Override
    public final List<RegionLocation> forEachInRegion(final double step) {
        return DoubleStream.iterate(min.x(), x -> x <= max.x(), x -> x + step)
            .boxed()
            .flatMap(x -> DoubleStream.iterate(min.z(), z -> z <= max.z(), z -> z + step)
                .boxed()
                .map(z -> new RegionLocation(min.world(), x, min.y(), z)))
            .collect(Collectors.toList());
    }

    @Override
    public final RegionLocation center() {
        return new RegionLocation(
            min.world(), 
            (min.x() + max.x()) / 2, 
            (min.y() + max.y()) / 2, 
            (min.z() + max.z()) / 2
        );
    }

    static final RegionFactory rectangle() {
        return (final RegionLocation location, final double... params) -> {
            final RegionLocation max = new RegionLocation(
                location.world(), 
                location.x() + params[0], 
                location.y(), 
                location.z() + params[1]
            );

            return new RegionTerrain(
                location, 
                max, 
                loc -> loc.x() >= location.x() && loc.x() <= max.x() &&
                       loc.z() >= location.z() && loc.z() <= max.z()
            );
        };
    }

    static final RegionFactory square() {
        return (final RegionLocation location, final double... params) -> {
            final double side = params[0];
            final RegionLocation max = new RegionLocation(
                location.world(), 
                location.x() + side, 
                location.y(), 
                location.z() + side
            );

            return new RegionTerrain(
                location, 
                max, 
                loc -> loc.x() >= location.x() && loc.x() <= max.x() &&
                       loc.z() >= location.z() && loc.z() <= max.z()
            );
        };
    }

    static final RegionFactory circle() {
        return (final RegionLocation location, final double... params) -> {
            final double radius = params[0];
            final RegionLocation min = new RegionLocation(
                location.world(), 
                location.x() - radius, 
                location.y(), 
                location.z() - radius
            );
            final RegionLocation max = new RegionLocation(
                location.world(), 
                location.x() + radius, 
                location.y(), 
                location.z() + radius
            );

            return new RegionTerrain(
                min, 
                max, 
                loc -> Math.pow(loc.x() - location.x(), 2) + 
                      Math.pow(loc.z() - location.z(), 2) <= Math.pow(radius, 2)
            );
        };
    }
}

