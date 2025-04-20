package dev.nautchkafe.region;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

final class RegionRenderer {

    private final RegionCuboid cuboid;
    private final int step;

    RegionRenderer(final RegionCuboid cuboid, final int step) {
        this.cuboid = cuboid;
        this.step = step;
    }

    void render(final Player player, final Particle particle) {
        final Chunk playerChunk = player.getlocationation().getChunk();
        final String world = cuboid.min().world();

        final BiConsumer<Location, Particle> showParticle = (location, part) -> {
            if (!location.getWorld().getName().equals(world)) {
                return;
            }

            if (!player.getWorld().equals(location.getWorld())) {
                return;
            }

            if (playerChunk.getX() - location.getChunk().getX() > 5) {
                return;
            }

            player.spawnParticle(part, location, 1, 0, 0, 0, 0);
        };

        cuboid.forEachInRegion(step).forEach(location -> showParticle.accept(location.toBukkit(), particle));
    }
}
