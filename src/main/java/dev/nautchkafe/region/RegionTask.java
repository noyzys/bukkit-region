package dev.nautchkafe.region;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

final class RegionTask {

    private final Plugin plugin;

    public RegionTask(final Plugin plugin) {
        this.plugin = plugin;
    }

    public void repeatAsync(final Runnable task, final long initialDelay, final long period) {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, task, initialDelay, period);
    }
}
