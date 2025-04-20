package dev.nautchkafe.region;

import java.util.function.Function;

final class RegionMath {

    public static final Function<Double, String> formatDistance = distance -> String.format("%.2f", distance);
}


