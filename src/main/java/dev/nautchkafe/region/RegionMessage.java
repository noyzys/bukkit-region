package dev.nautchkafe.region;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

final class RegionMessage {

    static final BiConsumer<Player, String> send = (player, message) ->
            player.sendMessage(Component.text(message));

    static final BiConsumer<Player, String> actionbar = (player, message) ->
            player.sendActionBar(Component.text(message));

    static final BiConsumer<Player, String> title = (player, message) -> {
        player.showTitle(Component.text(message), Component.empty());
    };
}
