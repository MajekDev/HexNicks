package me.majekdor.hexnicks;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HexNicks extends JavaPlugin {

    public static HexNicks instance;
    public DataManager nicknames;
    public HexNicks() {
        instance = this;
    }

    public void saveNicks() {
        for(String s : CommandNick.nicks.keySet()) {
            String value = CommandNick.nicks.get(s);
            this.nicknames.getConfig().set("nickData."+ s, value);
        }
        this.nicknames.saveConfig();
    }

    public void loadNicks() {
        CommandNick.nicks = new HashMap<>();
        if(this.nicknames.getConfig().contains("nickData")) {
            this.nicknames.getConfig().getConfigurationSection("nickData").getKeys(false).forEach(key -> {
                String value = (String) this.nicknames.getConfig().get("nickData." + key);
                CommandNick.nicks.put(key, value);
            });
        }
        this.nicknames.getConfig().set("nickData", null);
        this.nicknames.saveConfig();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.nicknames = new DataManager(this);
        loadNicks();
        final FileConfiguration config = this.getConfig(); this.saveDefaultConfig();
        this.getCommand("nick").setExecutor(new CommandNick());
        this.getCommand("nick").setTabCompleter(new Listen());
        this.getServer().getPluginManager().registerEvents(new Listen(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.saveNicks();
    }

    // Format hex color codes and standard minecraft color codes
    public static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
    public static String format(String msg) {
        if (Bukkit.getVersion().contains("1.16")) {
            Matcher match = pattern.matcher(msg);
            while (match.find()) {
                String color = msg.substring(match.start(), match.end());
                msg = msg.replace(color, ChatColor.of(color) + "");
                match = pattern.matcher(msg);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    // Remove hex and standard color codes
    public static String removeColorCodes(String msg) {
        StringBuilder sb = new StringBuilder(msg.length());
        char[] chars =  msg.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            if (chars[i] == '&' || chars[i] == org.bukkit.ChatColor.COLOR_CHAR) {
                ++i;
                continue;
            }
            if (chars[i] == '#') {
                i += 6;
                continue;
            }
            sb.append(chars[i]);
        }
        return sb.toString();
    }

}
