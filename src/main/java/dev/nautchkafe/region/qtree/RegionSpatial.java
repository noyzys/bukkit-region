package dev.nautchkafe.region.qtree;

import java.util.function.Predicate;
import java.util.stream.Stream;

@FunctionalInterface
public interface RegionSpatial {

    Stream<RegionLocation> query(final Region bounds);
    
    default RegionSpatial filter(final Predicate<RegionLocation> predicate) {
        return bounds -> this.query(bounds).filter(predicate);
    }
    
    static RegionSpatial of(final Region region) {
        return bounds -> region.forEachInRegion(1.0).stream()
            .filter(bounds::contains);
    }
}