package dev.nautchkafe.region;

@FunctionalInterface
interface RegionFactory {

    Region create(final RegionLocation location, final double... params);
}