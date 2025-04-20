package dev.nautchkafe.region;

import java.util.Map;

final class RegionFactorySelector {

    private static final Map<String, RegionFactory> FACTORIES = Map.of(
        "rectangle", RegionTerrain.rectangle(),
        "square", RegionTerrain.square(),
        "circle", RegionTerrain.circle()
    );

    static final Region createRegion(
        final String shapeType, 
        final RegionLocation location, 
        final double... params
    ) {
        return FACTORIES.getOrDefault(
            shapeType, 
            throwUnknownShape(shapeType)
        ).create(location, params);
    }

    private static final RegionFactory throwUnknownShape(final String shapeType) {
        return (final RegionLocation loc, final double... params) -> {
            throw new IllegalArgumentException("Unknown shape type: " + shapeType);
        };
    }
}