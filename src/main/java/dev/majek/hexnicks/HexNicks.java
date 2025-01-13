/*
 * This file is part of HexNicks, licensed under the MIT License.
 *
 * Copyright (c) 2020-2022 Majekdor
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.majek.hexnicks;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tchristofferson.configupdater.ConfigUpdater;
import dev.majek.hexnicks.api.HexNicksApi;
import dev.majek.hexnicks.command.*;
import dev.majek.hexnicks.config.ConfigValues;
import dev.majek.hexnicks.storage.*;
import dev.majek.hexnicks.event.PaperTabCompleteEvent;
import dev.majek.hexnicks.event.PlayerChat;
import dev.majek.hexnicks.event.PlayerJoin;
import dev.majek.hexnicks.hook.HookManager;
import dev.majek.hexnicks.util.LoggingManager;
import dev.majek.hexnicks.util.UpdateChecker;
import java.io.*;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Main plugin class.</p>
 * <p>Use {@link #core()} to access core plugin utilities such as nickname storage.</p>
 * <p>Use {@link #api()} to access api utilities such as event management.</p>
 */
public final class HexNicks extends JavaPlugin {

  private static HexNicks core;
  private static HexNicksApi api;
  private static LoggingManager logging;
  private static ConfigValues config;
  private static HookManager hooks;
  private static StorageMethod storage;
  private static TaskScheduler scheduler;
  private final File jsonFile;
  private final Map<UUID, Component> nickMap;
  private final Metrics metrics;
  private final UpdateChecker updateChecker;

  /**
   * Initialize plugin.
   * Logging must be instantiated before other managers.
   */
  public HexNicks() {
    core = this;
    api = new HexNicksApi();
    logging = new LoggingManager(this, new File(this.getDataFolder(), "logs"));
    config = new ConfigValues();
    hooks = new HookManager();
    scheduler = UniversalScheduler.getScheduler(core);
    this.jsonFile = new File(this.getDataFolder(), "nicknames.json");
    this.nickMap = new HashMap<>();
    // Track plugin metrics through bStats
    this.metrics = new Metrics(this, 8764);
    // Check for new versions on Spigot
    this.updateChecker = new UpdateChecker(this, 83554);
  }

  /**
   * Plugin startup logic.
   */
  @Override
  public void onEnable() {
    // Make sure we're on Paper 1.18.2+
    try {
      Class.forName("net.kyori.adventure.text.minimessage.MiniMessage");
    } catch (final ClassNotFoundException ignored) {
      logging.error("This plugin can only run on Paper 1.18.2+");
      this.getPluginLoader().disablePlugin(this);
    }

    // Make sure nickname storage file exists
    boolean dataFolderExists = this.getDataFolder().exists();
    if (!dataFolderExists) {
      boolean mkdirs = this.getDataFolder().mkdirs();
      if (!mkdirs) {
        logging.error("Failed to create plugin data folder.");
      }
    }
    boolean created = this.jsonFile.exists();
    if (!created) {
      try {
        created = this.jsonFile.createNewFile();
        if (created) {
          final PrintWriter writer = new PrintWriter(this.jsonFile);
          writer.write("{ }");
          writer.flush();
          writer.close();
        }
      } catch (final IOException ex) {
        logging.error("IOException while creating nicknames.json storage file.", ex);
      }
    }
    if (!created) {
      logging.error("Could not create nicknames.json storage file. Shutting down plugin.");
      this.getPluginLoader().disablePlugin(this);
    }

    // Load nicknames from storage
    if (this.getConfig().getBoolean("database-enabled")) {
      try {
        HikariManager.createTable();
        storage = new SqlStorage();
        storage.updateNicks();
        logging.log("Successfully connected to MySQL database.");
        HexNicks.scheduler().runTaskTimer(() -> HexNicks.storage().updateNicks(),
                200L, this.getConfig().getInt("update-interval", 300) * 20L);
      } catch (final SQLException ex) {
        logging.error("Failed to connect to MySQL database", ex);
        this.loadNicknamesFromJson();
      }
    } else {
      this.loadNicknamesFromJson();
    }

    // Register plugin commands
    this.registerCommands();

    // Initialize configuration file
    this.reload();

    // Custom chart to see what percentage of servers are supporting legacy
    this.metrics.addCustomChart(new SimplePie("supporting_legacy",
        () -> String.valueOf(config.LEGACY_COLORS)));
    // Custom chart to see what percentage of servers are using the built-in chat formatter
    this.metrics.addCustomChart(new SimplePie("using_chat_formatter",
        () -> String.valueOf(config.CHAT_FORMATTER)));

    // Register events
    this.registerEvents(new PlayerJoin(), new PaperTabCompleteEvent(), new PlayerChat());

    // Check for updates - prompt to update if there is one
    if (this.updateChecker.isBehindSpigot()) {
      logging.log("There is a new version of the plugin available! " +
          "Download it here: https://www.spigotmc.org/resources/83554/");
    }
  }

  private void loadNicknamesFromJson() {
    try {
      storage = new JsonStorage();
      JsonObject json = (JsonObject) JsonParser.parseReader(new FileReader(HexNicks.core().jsonFile()));
      for (final String key : json.keySet()) {
        this.nickMap.put(UUID.fromString(key), GsonComponentSerializer.gson()
                .deserializeFromTree(json.get(key)));
      }
    } catch (final IOException ex) {
      logging.error("Error loading nickname data from nicknames.json file", ex);
    }
    logging.log("Successfully loaded nicknames from Json storage.");
  }

  /**
   * Plugin shutdown logic.
   */
  @Override
  public void onDisable() {

  }

  /**
   * Register plugin commands.
   */
  @SuppressWarnings("ConstantConditions")
  private void registerCommands() {
    this.getCommand("nick").setExecutor(new CommandNick());
    this.getCommand("nick").setTabCompleter(new CommandNick());
    this.getCommand("nonick").setExecutor(new CommandNoNick());
    this.getCommand("nonick").setTabCompleter(new CommandNoNick());
    this.getCommand("hexnicks").setExecutor(new CommandHexNicks());
    this.getCommand("hexnicks").setTabCompleter(new CommandHexNicks());
    this.getCommand("realname").setExecutor(new CommandRealName());
    this.getCommand("realname").setTabCompleter(new CommandRealName());
    this.getCommand("nickother").setExecutor(new CommandNickOther());
    this.getCommand("nickother").setTabCompleter(new CommandNickOther());
    this.getCommand("nickcolor").setExecutor(new CommandNickColor());
    this.getCommand("nickcolor").setTabCompleter(new CommandNickColor());
  }

  /**
   * Register plugin events.
   */
  private void registerEvents(Listener... listeners) {
    for (final Listener listener : listeners) {
      this.getServer().getPluginManager().registerEvents(listener, this);
    }
  }

  /**
   * Get an instance of the main class. Use this for things like managing nicknames.
   *
   * @return main class
   */
  public static HexNicks core() {
    return core;
  }

  /**
   * Get the Nicks Api for accessing things such as event management and nickname lookup.
   *
   * @return api methods
   */
  public static HexNicksApi api() {
    return api;
  }

  /**
   * Easier access for plugin config options with defaults for redundancy.
   *
   * @return config values
   */
  public static ConfigValues config() {
    return config;
  }

  /**
   * Check what plugins this plugin has hooked into.
   *
   * @return plugin hook manager
   */
  public static HookManager hooks() {
    return hooks;
  }

  /**
   * Get the storage method the plugin is using for nickname storage.
   * This will return either {@link JsonStorage} or {@link SqlStorage}.
   *
   * @return nickname storage manager
   */
  public static StorageMethod storage() {
    return storage;
  }

  /**
   * Get the plugin's logging manager.
   *
   * @return logging manager
   */
  public static LoggingManager logging() {
    return logging;
  }

  /**
   * Get the plugin's scheduler. Depending on the type of server the plugin is running on,
   * this could be a Folia scheduler or a Bukkit scheduler.
   *
   * @return task scheduler
   */
  public static TaskScheduler scheduler() {
    return scheduler;
  }

  /**
   * Reload the plugin's configuration file.
   */
  public void reload() {
    HexNicks.logging().debug("Reloading plugin...");
    this.saveDefaultConfig();
    final File configFile = new File(this.getDataFolder(), "config.yml");
    try {
      ConfigUpdater.update(core, "config.yml", configFile, Collections.emptyList());
    } catch (final IOException ex) {
      logging.error("Failed to reload config.yml", ex);
    }
    this.reloadConfig();
    config.reload();
    storage.updateNicks();
    hooks.reloadHooks();
    logging.doDebug(config.DEBUG);
  }

  /**
   * Get the file where nicknames will be stored.
   *
   * @return json file
   */
  public @NotNull File jsonFile() {
    return this.jsonFile;
  }

  /**
   * Get the map that stores unique ids keyed to nicknames.
   *
   * @return nickname map
   */
  public @NotNull Map<UUID, Component> getNickMap() {
    return this.nickMap;
  }

  /**
   * Get a nickname from a player's display name.
   * If you want their nickname from storage use {@link HexNicks#storage()}
   *
   * @param player the player
   * @return nickname/display name
   */
  public @NotNull Component getDisplayName(@NotNull Player player) {
    return player.displayName().colorIfAbsent(config.DEFAULT_USERNAME_COLOR);
  }

  /**
   * Set a user's nickname using an online {@link Player}.
   * This will immediately be saved to Json.
   *
   * @param player the player
   * @param nick the player's new nickname
   */
  public void setNick(@NotNull Player player, @NotNull Component nick) {
    logging.debug("Setting " + player.getName() + "'s nickname to \n" +
        new GsonBuilder().setPrettyPrinting().create().toJson(
            GsonComponentSerializer.gson().serializeToTree(nick)
        )
    );
    this.nickMap.put(player.getUniqueId(), nick);
    player.displayName(nick);
    if (config.TAB_NICKS) {
      player.playerListName(nick);
    }
    storage.saveNick(player, nick);
    hooks.setEssNick(player, nick);
  }

  /**
   * Remove a nickname from the map and from Json storage.
   * It will be asynchronously removed from the file.
   *
   * @param player the player whose nickname to remove
   */
  public void removeNick(@NotNull Player player) {
    logging.debug("Removing " + player.getName() + "'s nickname.");
    this.nickMap.remove(player.getUniqueId());
    player.displayName(Component.text(player.getName()));
    if (config.TAB_NICKS) {
      player.playerListName(Component.text(player.getName()));
    }
    storage.removeNick(player.getUniqueId());
    hooks.setEssNick(player, Component.text(player.getName()));
  }

  /**
   * Check if the plugin has a new update on Spigot.
   *
   * @return whether there's an update
   */
  public boolean hasUpdate() {
    return this.updateChecker.isBehindSpigot();
  }
}
