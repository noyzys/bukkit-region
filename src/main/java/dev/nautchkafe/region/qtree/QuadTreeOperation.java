package dev.nautchkafe.region.qtree;

import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class QuadTreeOperation {

    private static final QuadTreeOperation CORE_OPERATIONS = new QuadTreeOperation();

    public static final Function<Region, RegionSpatial> create = 
        CORE_OPERATIONS::createQuadTree;

    public static final BiFunction<RegionSpatial, Predicate<RegionLocation>, RegionSpatial> filtered = 
        (query, predicate) -> query.filter(predicate);

    public static final Function<RegionSpatial, RegionSpatial> optimized = 
        query -> bounds -> query.query(bounds)
            .sorted(Comparator.comparingDouble(loc -> 
                interleaveBits((int)loc.x(), (int)loc.z())
            ));

    // mutable non fp
    private static long interleaveBits(final int x, final int z) {
        long result = 0;
        for (final int i = 0; i < 32; i++) {
            result |= ((x & (1 << i)) << i) | ((z & (1 << i)) << (i + 1));
        }
        
        return result;
    }
}