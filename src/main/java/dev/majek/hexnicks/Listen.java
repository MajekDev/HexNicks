package dev.majek.hexnicks;

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

import java.util.Collections;
import java.util.List;

public class Listen implements Listener, TabCompleter {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        FileConfiguration c = HexNicks.instance.getConfig();
        Player p = e.getPlayer();
        if (!c.getBoolean("database-enabled")) {
            if (CommandNick.nicks.containsKey(p.getUniqueId()))
                p.setDisplayName(TextUtils.applyColorCodes(CommandNick.nicks.get(p.getUniqueId()) + "&r&f"));
        }
        if (HexNicks.instance.SQL.isConnected()) {
            HexNicks.instance.data.createPlayer(p);
            if (HexNicks.instance.data.getNickname(p.getUniqueId()) != null)
                CommandNick.nicks.put(p.getUniqueId(), HexNicks.instance.data.getNickname(p.getUniqueId()) + "&r&f");
                p.setDisplayName(TextUtils.applyColorCodes(HexNicks.instance.data.getNickname(p.getUniqueId()) + "&r&f"));
        }
        if (CommandNick.nicks.containsKey(p.getUniqueId())) {
            if (c.getBoolean("joinleave-message-nicks"))
                e.setJoinMessage(TextUtils.applyColorCodes((c.getString("join-message-format")).replace("%nickname%", CommandNick.nicks.get(p.getUniqueId()))));
            if (c.getBoolean("tab-nicknames")) {
                p.setPlayerListName(TextUtils.applyColorCodes(CommandNick.nicks.get(p.getUniqueId())));
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        FileConfiguration c = HexNicks.instance.getConfig();
        Player p = e.getPlayer();
        if (CommandNick.nicks.containsKey(p.getUniqueId())) {
            if (c.getBoolean("joinleave-message-nicks"))
                e.setQuitMessage(TextUtils.applyColorCodes((c.getString("leave-message-format")).replace("%nickname%", CommandNick.nicks.get(p.getUniqueId()))));
        } else {
            if (c.getBoolean("joinleave-message-nicks"))
                e.setQuitMessage(TextUtils.applyColorCodes((c.getString("leave-message-format")).replace("%nickname%", p.getDisplayName())));
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        FileConfiguration c = HexNicks.instance.getConfig();
        String message = e.getDeathMessage();
        Player p = e.getEntity(); Player killer = p.getKiller();
        if (CommandNick.nicks.containsKey(p.getUniqueId())) {
            if (c.getBoolean("death-message-nicks")) {
                if (killer != null) {
                    e.setDeathMessage(TextUtils.applyColorCodes(message.replace(killer.getName(), CommandNick.nicks.get(killer.getUniqueId()) + "&r")
                            .replace(p.getName(), CommandNick.nicks.get(p.getUniqueId()) + "&r")));
                } else {
                    e.setDeathMessage(TextUtils.applyColorCodes(message.replace(p.getName(), CommandNick.nicks.get(p.getUniqueId()) + "&r")));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (!HexNicks.instance.getConfig().getBoolean("format-chat"))
            return;
        String message = e.getMessage();
        Player player = e.getPlayer();
        if (HexNicks.instance.getConfig().getBoolean("use-permissions")) {
            if (player.hasPermission("hexnicks.chat"))
                e.setMessage(TextUtils.applyColorCodes(message));
        } else
            e.setMessage(TextUtils.applyColorCodes(message));

    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias,
                                                @NotNull String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
