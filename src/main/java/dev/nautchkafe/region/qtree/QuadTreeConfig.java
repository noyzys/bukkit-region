package dev.nautchkafe.region.qtree;

public record QuadTreeConfig(
    int capacity,
    double minX,
    double minZ,
    double maxX,
    double maxZ
) {

    public static final int DEFAULT_CAPACITY = 4;
}