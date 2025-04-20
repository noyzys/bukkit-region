package dev.nautchkafe.region;

import java.util.List;
import java.util.function.Function;

interface Region {

    boolean contains(final RegionLocation location);

    Region applyToBounds(final Function<Double, Double> operation);

    List<RegionLocation> forEachInRegion(final double step);

    RegionLocation center();
}