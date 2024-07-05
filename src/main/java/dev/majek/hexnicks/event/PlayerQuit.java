package dev.majek.hexnicks.event;

import dev.majek.hexnicks.HexNicks;
import dev.majek.hexnicks.config.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Set the joining player's nickname to their stored nickname if they have one
        Player player = event.getPlayer();
        HexNicks.storage().hasNick(player.getUniqueId()).whenCompleteAsync((aBoolean, throwable) -> {
            if (aBoolean) {
                HexNicks.storage().getNick(player.getUniqueId()).whenCompleteAsync((component, throwable1) -> {
                    ComponentBuilder textBuilder = Component.text()
                            .append(Component.text("<", Style.empty().color(TextColor.color(0xFFFFFF))))
                            .append(component)
                            .append(Component.text(">", Style.empty().color(TextColor.color(0xFFFFFF))))
                            .append(Component.text(" disconnected!", Style.empty().color(TextColor.color(0xFFFF55))));

                    event.quitMessage(textBuilder.build());
                });
            } else {
                ComponentBuilder textBuilder = Component.text()
                        .append(Component.text("<", Style.empty().color(TextColor.color(0xFFFFFF))))
                        .append(Component.text(player.getName()))
                        .append(Component.text(">", Style.empty().color(TextColor.color(0xFFFFFF))))
                        .append(Component.text(" disconnected!", Style.empty().color(TextColor.color(0xFFFF55))));
                event.quitMessage(textBuilder.build());
            }
        });
    }
}
