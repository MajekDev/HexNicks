package dev.majek.hexnicks;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HexNicks extends JavaPlugin implements Listener {

    public static HexNicks instance;
    public MySQL SQL;
    public SQLGetter data;
    public JsonConfig jsonConfig;
    public HexNicks() {
        instance = this;
    }

    /** Pattern matching "nicer" legacy hex chat color codes - &#rrggbb */
    private static final Pattern NICER_HEX_COLOR_PATTERN = Pattern.compile("&#([0-9a-fA-F]{6})");
    public static final List<Character> COLOR_CHARS = Arrays.asList('0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'l', 'm', 'n', 'o', 'r', 'x');

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
                e.printStackTrace();
                getLogger().severe("[HexNicks] Failed to connect to database.");
            }
        }
        if (SQL.isConnected()) {
            getLogger().info("[HexNicks] Successfully connected to database.");
            data.createTable();
        } else
            loadNicksFromJSON();

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
        if (c.getBoolean("database-enabled"))
            SQL.disconnect();
    }

    public void loadNicksFromJSON() {
        File jsonFile = new File(getDataFolder(), "nicknames.json");
        jsonConfig = new JsonConfig(getDataFolder(), "nicknames");
        if (!jsonFile.exists())
            jsonConfig.createConfig();
        try {
            JSONObject jsonObject = jsonConfig.toJSONObject();
            Map<String, Object> configMap = new Gson().fromJson(
                    jsonObject.toString(), new TypeToken<HashMap<String, Object>>() {}.getType()
            );
            for (String s : configMap.keySet())
                CommandNick.nicks.put(UUID.fromString(s), configMap.get(s).toString());
        } catch (IOException | ParseException e) {
            getLogger().severe("Error loading nicknames.json data file.");
            e.printStackTrace();
        }
    }

    public static String removeColorCodes(String message) {
        // Colorize it first to properly strip hex codes
        message = colorize(message);
        StringBuilder sb = new StringBuilder(message.length());
        char[] chars = message.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            if (chars[i] == '&' || chars[i] == ChatColor.COLOR_CHAR &&
                    i < chars.length - 1 && COLOR_CHARS.contains(chars[i + 1])) {
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
