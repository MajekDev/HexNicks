package me.majekdor.hexnicks;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class Listen implements Listener, TabCompleter {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (CommandNick.nicks.containsKey(p.getName())) {
            p.setDisplayName(HexNicks.format(CommandNick.nicks.get(p.getName()) + "&r"));
            p.setPlayerListName(HexNicks.format(CommandNick.nicks.get(p.getName())));
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        String message = e.getMessage();
        e.setMessage(HexNicks.format(message));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias,
                                                @NotNull String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
