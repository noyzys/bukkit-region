package dev.nautchkafe.region;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

final class RegionSpawner implements Region {

    private final RegionLocation min;
    private final RegionLocation max;
    private final BiPredicate<Double, Double> shapePredicate;

    RegionSpawner(
        final RegionLocation min,
        final RegionLocation max,
        final BiPredicate<Double, Double> shapePredicate
    ) {
        this.min = min;
        this.max = max;
        this.shapePredicate = shapePredicate;
    }

    @Override
    public boolean contains(final RegionLocation location) {
        return shapePredicate.test(location.x(), location.z());
    }

    @Override
    public Region applyToBounds(final Function<Double, Double> operation) {
        return new RegionSpawner(
            new RegionLocation(min.world(), operation.apply(min.x()), operation.apply(min.y()), operation.apply(min.z())),
            new RegionLocation(max.world(), operation.apply(max.x()), operation.apply(max.y()), operation.apply(max.z())),
            shapePredicate
        );
    }

    @Override
    public List<RegionLocation> forEachInRegion(final double step) {
        return DoubleStream.iterate(min.x(), x -> x <= max.x(), x -> x + step)
            .boxed()
            .flatMap(x -> DoubleStream.iterate(min.z(), z -> z <= max.z(), z -> z + step)
                .boxed()
                .filter(z -> shapePredicate.test(x, z))
                .map(z -> new RegionLocation(min.world(), x, min.y(), z)))
            .collect(Collectors.toList());
    }

    @Override
    public RegionLocation center() {
        return new RegionLocation(
            min.world(),
            (min.x() + max.x()) / 2,
            (min.y() + max.y()) / 2,
            (min.z() + max.z()) / 2
        );
    }

    static RegionSpawner of(
        final RegionLocation min,
        final RegionLocation max,
        final BiPredicate<Double, Double> predicate
    ) {
        return new RegionSpawner(min, max, predicate);
    }
}
