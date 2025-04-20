package dev.nautchkafe.region.qtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import java.util.function.Predicate;

final class QuadTreeNode {

    private final QuadTreeConfig config;
    private final List<RegionLocation> points;
    private QuadTreeNode[] children;

    QuadTreeNode(final QuadTreeConfig config) {
        this.config = config;
        this.points = new ArrayList<>(config.capacity());
    }

    void insert(final RegionLocation point) {
        if (!contains(point)) {
            return;
        }

        if (shouldInsertDirectly()) {
            points.add(point);
            return;
        }

        ensureChildren();
        insertToChildren(point);
    }

    Stream<RegionLocation> query(final Region bounds) {
        if (!intersects(bounds)) {
            return Stream.empty();
        }

        return Stream.concat(
            queryCurrentPoints(bounds),
            queryChildren(bounds)
        );
    }

    private boolean contains(final RegionLocation location) {
        return location.x() >= config.minX() && location.x() <= config.maxX() && 
               location.z() >= config.minZ() && location.z() <= config.maxZ();
    }

    private boolean intersects(final Region bounds) {
        return !(bounds.max().x() < config.minX() || bounds.min().x() > config.maxX() ||
                 bounds.max().z() < config.minZ() || bounds.min().z() > config.maxZ());
    }

    private boolean shouldInsertDirectly() {
        return children == null && points.size() < config.capacity();
    }

    private void ensureChildren() {
        if (children == null) {
            split();
        }
    }

    private void split() {
        final double midX = (config.minX() + config.maxX()) / 2;
        final double midZ = (config.minZ() + config.maxZ()) / 2;

        children = new QuadTreeNode[]{
            createChild(config.minX(), config.minZ(), midX, midZ),
            createChild(midX, config.minZ(), config.maxX(), midZ),
            createChild(config.minX(), midZ, midX, config.maxZ()),
            createChild(midX, midZ, config.maxX(), config.maxZ())
        };

        redistributePoints();
    }

    private QuadTreeNode createChild(final double x1, final double z1, final double x2, final double z2) {
        return new QuadTreeNode(new QuadTreeConfig(
            config.capacity(),
            x1, z1, x2, z2
        ));
    }

    private void redistributePoints() {
        points.forEach(this::insertToChildren);
        points.clear();
    }

    private void insertToChildren(final RegionLocation point) {
        for (final QuadTreeNode child : children) {
            child.insert(point);
        }
    }

    private Stream<RegionLocation> queryCurrentPoints(final Region bounds) {
        return points.stream().filter(bounds::contains);
    }

    private Stream<RegionLocation> queryChildren(final Region bounds) {
        return children != null 
            ? Arrays.stream(children).flatMap(child -> child.query(bounds))
            : Stream.empty();
    }
}