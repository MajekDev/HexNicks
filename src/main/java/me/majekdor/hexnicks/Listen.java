package me.majekdor.hexnicks;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class Listen implements Listener, TabCompleter {

    FileConfiguration c = HexNicks.instance.getConfig();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String message = e.getJoinMessage();
        if (CommandNick.nicks.containsKey(p.getName())) {
            p.setDisplayName(HexNicks.format(CommandNick.nicks.get(p.getName()) + "&r"));
            if (c.getBoolean("joinleave-message-nicks")) {
                e.setJoinMessage(HexNicks.format((c.getString("join-message-format")).replace("%nickname%", CommandNick.nicks.get(p.getName()))));
            }
            if (c.getBoolean("tab-nicknames")) {
                p.setPlayerListName(HexNicks.format(CommandNick.nicks.get(p.getName())));
            }
        } else {
            e.setJoinMessage(HexNicks.format((c.getString("join-message-format")).replace("%nickname%", p.getDisplayName())));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer(); String message = e.getQuitMessage();
        if (CommandNick.nicks.containsKey(p.getName())) {
            if (c.getBoolean("joinleave-message-nicks")) {
                e.setQuitMessage(HexNicks.format((c.getString("leave-message-format")).replace("%nickname%", CommandNick.nicks.get(p.getName()))));
            }
        } else {
            e.setQuitMessage(HexNicks.format((c.getString("leave-message-format")).replace("%nickname%", p.getDisplayName())));
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        String message = e.getDeathMessage();
        Player p = e.getEntity(); Player killer = p.getKiller();
        if (CommandNick.nicks.containsKey(p.getName())) {
            if (c.getBoolean("death-message-nicks")) {
                if (killer != null) {
                    e.setDeathMessage(HexNicks.format(message.replace(killer.getName(), CommandNick.nicks.get(killer.getName()) + "&r")
                            .replace(p.getName(), CommandNick.nicks.get(p.getName()) + "&r")));
                } else {
                    e.setDeathMessage(HexNicks.format(message.replace(p.getName(), CommandNick.nicks.get(p.getName()) + "&r")));
                }
            }
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
