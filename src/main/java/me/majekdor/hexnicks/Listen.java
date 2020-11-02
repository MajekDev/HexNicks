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
        if (!c.getBoolean("database-enabled")) {
            p.setDisplayName(HexNicks.format(CommandNick.nicks.get(p.getUniqueId()) + "&r"));
        }
        if (HexNicks.instance.SQL.isConnected()) {
            HexNicks.instance.data.createPlayer(p);
            if (HexNicks.instance.data.getNickname(p.getUniqueId()) != null)
                CommandNick.nicks.put(p.getUniqueId(), HexNicks.instance.data.getNickname(p.getUniqueId()) + "&r");
                p.setDisplayName(HexNicks.format(HexNicks.instance.data.getNickname(p.getUniqueId()) + "&r"));
        }
        if (CommandNick.nicks.containsKey(p.getUniqueId())) {
            if (c.getBoolean("joinleave-message-nicks"))
                e.setJoinMessage(HexNicks.format((c.getString("join-message-format")).replace("%nickname%", CommandNick.nicks.get(p.getUniqueId()))));
            if (c.getBoolean("tab-nicknames")) {
                p.setPlayerListName(HexNicks.format(CommandNick.nicks.get(p.getUniqueId())));
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer(); String message = e.getQuitMessage();
        if (CommandNick.nicks.containsKey(p.getUniqueId())) {
            if (c.getBoolean("joinleave-message-nicks"))
                e.setQuitMessage(HexNicks.format((c.getString("leave-message-format")).replace("%nickname%", CommandNick.nicks.get(p.getUniqueId()))));
        } else {
            if (c.getBoolean("joinleave-message-nicks"))
                e.setQuitMessage(HexNicks.format((c.getString("leave-message-format")).replace("%nickname%", p.getDisplayName())));
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        String message = e.getDeathMessage();
        Player p = e.getEntity(); Player killer = p.getKiller();
        if (CommandNick.nicks.containsKey(p.getUniqueId())) {
            if (c.getBoolean("death-message-nicks")) {
                if (killer != null) {
                    e.setDeathMessage(HexNicks.format(message.replace(killer.getName(), CommandNick.nicks.get(killer.getUniqueId()) + "&r")
                            .replace(p.getName(), CommandNick.nicks.get(p.getUniqueId()) + "&r")));
                } else {
                    e.setDeathMessage(HexNicks.format(message.replace(p.getName(), CommandNick.nicks.get(p.getUniqueId()) + "&r")));
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
