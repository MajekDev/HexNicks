package dev.majek.hexnicks;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public final class HexNicks extends JavaPlugin implements Listener {

    public static HexNicks instance;
    public MySQL SQL;
    public SQLGetter data;
    public DataManager nicknames;
    public JsonConfig jsonConfig;
    public HexNicks() {
        instance = this;
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
                e.printStackTrace();
                getLogger().severe("[HexNicks] Failed to connect to database.");
            }
        }
        if (SQL.isConnected()) {
            getLogger().info("[HexNicks] Successfully connected to database.");
            data.createTable();
        } else
            loadNicksFromJSON();

        File legacyStorageFile = new File(instance.getDataFolder(), "data.yml");
        if (legacyStorageFile.exists()) {
            this.nicknames = new DataManager(this);
            Map<UUID, String> legacyNicks = loadLegacyNicks();
            for (UUID uuid : legacyNicks.keySet()) {
                String nickname = legacyNicks.get(uuid);
                CommandNick.nicks.put(uuid, TextUtils.applyColorCodes(nickname));

                try {
                    HexNicks.instance.jsonConfig.putInJSONObject(uuid, TextUtils.applyColorCodes(nickname));
                } catch (IOException | ParseException e) {
                    HexNicks.instance.getLogger().severe("Error saving nickname to nicknames.json data file.");
                    e.printStackTrace();
                }
            }
            boolean deleted = legacyStorageFile.delete();
            if (deleted)
                getLogger().info("Deleted legacy yml storage.");
            else
                getLogger().severe("Error deleting legacy yml storage!");
        }

        new Metrics(this, 8764); // Metric stuffs

        this.saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            ConfigUpdater.update(instance, "config.yml", configFile, Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.reloadConfig();

        if (this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") &&
                this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("Hooking into PlaceholderAPI...");
            new PlaceholderAPI(this).register();
        }

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
        if (!jsonFile.exists()) {
            jsonConfig.createConfig();
            try {
                FileWriter writer = new FileWriter(jsonFile);
                writer.write("{}");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                getLogger().severe("Error initializing nicknames.json data file.");
                e.printStackTrace();
            }
        }
        try {
            JSONObject jsonObject = jsonConfig.toJSONObject();
            Map<String, Object> configMap = new GsonBuilder().setPrettyPrinting().create().fromJson(
                    jsonObject.toString(), new TypeToken<HashMap<String, Object>>() {}.getType()
            );
            for (String s : configMap.keySet())
                CommandNick.nicks.put(UUID.fromString(s), configMap.get(s).toString());
        } catch (IOException | ParseException e) {
            getLogger().severe("Error loading nicknames.json data file.");
            e.printStackTrace();
        }
    }

    @Deprecated
    public Map<UUID, String> loadLegacyNicks() {
        Map<UUID, String> legacyNicks = new HashMap<>();
        if(this.nicknames.getConfig().contains("nickData")) {
            this.nicknames.getConfig().getConfigurationSection("nickData").getKeys(false).forEach(key -> {
                String value = (String) this.nicknames.getConfig().get("nickData." + key);
                legacyNicks.put(UUID.fromString(key), value);
            });
        }
        this.nicknames.getConfig().set("nickData", null);
        this.nicknames.saveConfig();
        if (SQL.isConnected()) {
            for (UUID uuid : legacyNicks.keySet()) {
                Player p = Bukkit.getPlayer(uuid);
                if (p != null) {
                    data.createPlayer(p);
                    data.addNickname(p.getUniqueId(), legacyNicks.get(uuid));
                }
            }
        }
        return legacyNicks;
    }
}
