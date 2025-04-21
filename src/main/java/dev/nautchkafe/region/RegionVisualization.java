package dev.nautchkafe.region;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.location.Location;

@FunctionalInterface
interface RegionVisualization {

    void accept(final Region region, final Player player, final Material borderMaterial, final long displayTicks);
}

final class RegionVisualizer {

    static final RegionVisualization visualize = (region, player, borderMaterial, displayTicks) -> {
        final double yOffset = 0.5;
        final BlockData borderData = borderMaterial.createBlockData();
        final World world = Bukkit.getWorld(region.center().world());

        final Function<RegionLocation, Location> toDisplayLocation = loc ->
        new Location(world, loc.x(), loc.y() + yOffset, loc.z());

        final Predicate<RegionLocation> isBorder = loc -> {
            RegionLocation testLoc = new RegionLocation(loc.world(), loc.x(), loc.y(), loc.z());
            return !region.contains(testLoc.withX(loc.x() + 1)) ||
            !region.contains(testLoc.withX(loc.x() - 1)) ||
            !region.contains(testLoc.withZ(loc.z() + 1)) ||
            !region.contains(testLoc.withZ(loc.z() - 1));
        };

        final Consumer<RegionLocation> renderBorder = loc ->
                player.sendBlockChange(toDisplayLocation.apply(loc), borderData);

        region.forEachInRegion(1.0).stream()
        .filter(isBorder)
        .forEach(renderBorder);

        // or RegionTask
        Bukkit.getScheduler().runTaskLater(
            Bukkit.getPluginManager().getPlugins()[0],
            () -> clearVisualization(region, player),
            displayTicks
        );
    };

    private static void clearVisualization(final Region region, final Player player) {
        final World world = Bukkit.getWorld(region.center().world());

        region.forEachInRegion(1.0).forEach(loc -> {
            final Block realBlock = world.getBlockAt(
                (int) loc.x(),
                (int) (loc.y() + 0.5),
                (int) loc.z()
            );
            player.sendBlockChange(realBlock.getLocation(), realBlock.getBlockData());
        });
    }
}
