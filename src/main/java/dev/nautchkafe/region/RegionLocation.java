package dev.nautchkafe.region;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

record RegionLocation(
    String world, 
    double x, 
    double y, 
    double z
) {

    public static RegionLocation fromBukkit(final Location location) {
        return new RegionLocation(location.getWorld().getName(), 
            location.getX(), location.getY(), location.getZ());
    }

    public Location toBukkit() {
        final World world = Bukkit.getWorld(this.world);
        return new Location(world, this.x, this.y, this.z);
    }
}