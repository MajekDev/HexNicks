package dev.majek.hexnicks;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HexNicks extends JavaPlugin implements Listener {

    public static HexNicks instance;
    public MySQL SQL;
    public SQLGetter data;
    public DataManager nicknames;
    public HexNicks() {
        instance = this;
    }

    /** Pattern matching "nicer" legacy hex chat color codes - &#rrggbb */
    private static final Pattern NICER_HEX_COLOR_PATTERN = Pattern.compile("&#([0-9a-fA-F]{6})");

    public void saveNicks() {
        for(UUID uuid : CommandNick.nicks.keySet()) {
            String value = CommandNick.nicks.get(uuid);
            this.nicknames.getConfig().set("nickData."+ uuid.toString(), value);
        }
        this.nicknames.saveConfig();
    }

    public void loadNicks() {
        CommandNick.nicks = new HashMap<>();
        if(this.nicknames.getConfig().contains("nickData")) {
            this.nicknames.getConfig().getConfigurationSection("nickData").getKeys(false).forEach(key -> {
                String value = (String) this.nicknames.getConfig().get("nickData." + key);
                CommandNick.nicks.put(UUID.fromString(key), value);
            });
        }
        this.nicknames.getConfig().set("nickData", null);
        this.nicknames.saveConfig();
        if (SQL.isConnected()) {
            for (UUID uuid : CommandNick.nicks.keySet()) {
                Player p = Bukkit.getPlayer(uuid);
                if (p != null) {
                    data.createPlayer(p);
                    data.addNickname(p.getUniqueId(), CommandNick.nicks.get(uuid));
                }
            }
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onEnable() {
        // Plugin startup logic
        FileConfiguration c = HexNicks.instance.getConfig();
        this.SQL = new MySQL();
        this.data = new SQLGetter(this);
        if (c.getBoolean("database-enabled")) {
            try {
                SQL.connect();
            } catch (ClassNotFoundException | SQLException e) {
                //e.printStackTrace();
                Bukkit.getLogger().info("[HexNicks] Failed to connect to database.");
            }
            if (SQL.isConnected()) {
                Bukkit.getLogger().info("[HexNicks] Successfully connected to database.");
                data.createTable();
            }
        }
        this.nicknames = new DataManager(this);
        loadNicks();
        new Metrics(this, 8764); // Metric stuffs

        this.saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            ConfigUpdater.update(instance, "config.yml", configFile, Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.reloadConfig();

        this.getCommand("nick").setExecutor(new CommandNick());
        this.getCommand("nonick").setExecutor(new CommandNick());
        this.getCommand("nickcolor").setExecutor(new CommandNick());
        this.getCommand("nickcolor").setTabCompleter(new Listen());
        this.getCommand("nick").setTabCompleter(new Listen());
        this.getCommand("hexreload").setExecutor(new CommandNick());
        this.getServer().getPluginManager().registerEvents(new Listen(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        FileConfiguration c = HexNicks.instance.getConfig();
        if (c.getBoolean("database-enabled")) {
            SQL.disconnect();
        } else {
            this.saveNicks();
        }
    }

    // Remove hex and standard color codes
    public static String removeColorCodes(String msg) {
        StringBuilder sb = new StringBuilder(msg.length());
        char[] chars =  msg.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            if (chars[i] == '&' || chars[i] == ChatColor.COLOR_CHAR) {
                ++i;
                continue;
            }
            sb.append(chars[i]);
        }
        return sb.toString();
    }

    /**
     * Translates color codes in the given input string.
     *
     * @param string the string to "colorize"
     * @return the colorized string
     */
    public static String colorize(String string) {
        if (string == null)
            return "null";

        // Convert from the '&#rrggbb' hex color format to the '&x&r&r&g&g&b&b' one used by Bukkit.
        Matcher matcher = NICER_HEX_COLOR_PATTERN.matcher(string);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            StringBuilder replacement = new StringBuilder(14).append("&x");
            for (char character : matcher.group(1).toCharArray())
                replacement.append('&').append(character);
            matcher.appendReplacement(sb, replacement.toString());
        }
        matcher.appendTail(sb);

        // Translate from '&' to '§' (section symbol)
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', sb.toString());
    }
}
