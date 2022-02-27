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

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.tchristofferson.configupdater.ConfigUpdater;
import dev.majek.hexnicks.api.NicksApi;
import dev.majek.hexnicks.command.*;
import dev.majek.hexnicks.config.JsonConfig;
import dev.majek.hexnicks.config.NicksConfig;
import dev.majek.hexnicks.config.NicksSql;
import dev.majek.hexnicks.event.PaperTabCompleteEvent;
import dev.majek.hexnicks.event.PlayerJoin;
import dev.majek.hexnicks.hook.NicksHooks;
import dev.majek.hexnicks.server.PaperServer;
import dev.majek.hexnicks.server.ServerSoftware;
import dev.majek.hexnicks.server.SpigotServer;
import dev.majek.hexnicks.storage.JsonStorage;
import dev.majek.hexnicks.storage.SqlStorage;
import dev.majek.hexnicks.storage.StorageMethod;
import dev.majek.hexnicks.util.LoggingManager;
import dev.majek.hexnicks.util.NicksUtils;
import dev.majek.hexnicks.util.UpdateChecker;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Main plugin class.</p>
 * <p>Use {@link #core()} to access core plugin utilities such as nickname storage.</p>
 * <p>Use {@link #api()} to access api utilities such as event management.</p>
 */
public final class Nicks extends JavaPlugin {

  private static Nicks                core;
  private static NicksApi             api;
  private static NicksUtils           utils;
  private static NicksConfig          config;
  private static NicksSql             sql;
  private static NicksHooks           hooks;
  private static ServerSoftware       software;
  private static StorageMethod        storage;
  private static LoggingManager       logging;
  private final JsonConfig            jsonConfig;
  private final Map<UUID, Component>  nickMap;
  private final Metrics               metrics;
  private final UpdateChecker         updateChecker;

  /**
   * Initialize plugin.
   */
  public Nicks() {
    core = this;
    api = new NicksApi();
    utils = new NicksUtils();
    config = new NicksConfig();
    sql = new NicksSql();
    hooks = new NicksHooks();
    logging = new LoggingManager(this, new File(this.getDataFolder(), "logs"));
    this.jsonConfig = new JsonConfig(this.getDataFolder(), "nicknames.json");
    try {
      this.jsonConfig.createConfig();
    } catch (final FileNotFoundException ex) {
      logging.error("Error creating nicknames.json file", ex);
    }
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
    // Get server software
    try {
      Class.forName("io.papermc.paper.event.player.AsyncChatEvent");
      software = new PaperServer();
    } catch (final ClassNotFoundException ignored) {
      software = new SpigotServer();
      logging.log("This plugin will run better on PaperMC 1.17+!");
    }
    logging.log("Running on " + software.softwareName() + " server software.");

    // Load nicknames from storage
    if (this.getConfig().getBoolean("database-enabled")) {
      try {
        sql.connect();
      } catch (final SQLException ex) {
        logging.error("Failed to connect to MySQL database", ex);
      }
    }
    if (sql.isConnected()) {
      logging.log("Successfully connected to MySQL database.");
      storage = new SqlStorage();
      sql.createTable();
      storage.updateNicks();
    } else {
      try {
        storage = new JsonStorage();
        final JsonObject jsonObject = this.jsonConfig.toJsonObject();
        for (final String key : jsonObject.keySet()) {
          this.nickMap.put(UUID.fromString(key), GsonComponentSerializer.gson()
              .deserializeFromTree(jsonObject.get(key)));
        }
      } catch (final IOException ex) {
        logging.error("Error loading nickname data from nicknames.json file", ex);
      }
      logging.log("Successfully loaded nicknames from Json storage.");
    }

    // Set debug status
    logging.doDebug(config.DEBUG);

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
    this.registerEvents(new PlayerJoin(), software);

    // Check for updates - prompt to update if there is one
    if (this.updateChecker.isBehindSpigot()) {
      logging.log("There is a new version of the plugin available! " +
          "Download it here: https://www.spigotmc.org/resources/83554/");
    }
  }

  /**
   * Plugin shutdown logic.
   */
  @Override
  public void onDisable() {
    // Disconnect from Sql if necessary
    if (sql.isConnected()) {
      sql.disconnect();
    }
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
    this.getCommand("realname").setExecutor(new CommandRealName());
    this.getCommand("realname").setTabCompleter(new CommandRealName());
    this.getCommand("nickother").setExecutor(new CommandNickOther());
    this.getCommand("nickother").setTabCompleter(new CommandNickOther());
    this.getCommand("nickcolor").setExecutor(new CommandNickColor());
    this.getCommand("nickcolor").setTabCompleter(new CommandNickColor());
    this.getCommand("nicksreload").setExecutor(new CommandNicksReload());
    this.getCommand("nicksreload").setTabCompleter(new CommandNicksReload());
  }

  /**
   * Register plugin events.
   */
  private void registerEvents(Listener... listeners) {
    for (final Listener listener : listeners) {
      this.getServer().getPluginManager().registerEvents(listener, this);
    }
    if (software instanceof PaperServer) {
      this.getServer().getPluginManager().registerEvents(new PaperTabCompleteEvent(), this);
    }
  }

  /**
   * Get an instance of the main class. Use this for things like managing nicknames.
   *
   * @return Core.
   */
  public static Nicks core() {
    return core;
  }

  /**
   * Get the Nicks Api for accessing things such as event management and nickname lookup.
   *
   * @return Api.
   */
  public static NicksApi api() {
    return api;
  }

  /**
   * Access various utility methods used in the plugin.
   *
   * @return NicksUtils.
   */
  public static NicksUtils utils() {
    return utils;
  }

  /**
   * Easier access for plugin config options with defaults for redundancy.
   *
   * @return NicksConfig.
   */
  public static NicksConfig config() {
    return config;
  }

  /**
   * Access the plugin's SQL connection if connected.
   *
   * @return NicksSql.
   */
  public static NicksSql sql() {
    return sql;
  }

  /**
   * Check what plugins this plugin has hooked into.
   *
   * @return NicksHooks.
   */
  public static NicksHooks hooks() {
    return hooks;
  }

  /**
   * Get the server software the plugin is running on.
   * This will return either {@link PaperServer} or {@link SpigotServer}.
   *
   * @return ServerSoftware.
   */
  public static ServerSoftware software() {
    return software;
  }

  /**
   * Get the storage method the plugin is using for nickname storage.
   * This will return either {@link JsonStorage} or {@link SqlStorage}.
   *
   * @return StorageMethod.
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
   * Reload the plugin's configuration file.
   */
  public void reload() {
    Nicks.logging().debug("Reloading plugin...");
    this.saveDefaultConfig();
    final File configFile = new File(this.getDataFolder(), "config.yml");
    try {
      ConfigUpdater.update(core, "config.yml", configFile, Collections.emptyList());
    } catch (final IOException ex) {
      ex.printStackTrace();
    }
    this.reloadConfig();
    config.reload();
    storage.updateNicks();
    hooks.reloadHooks();
  }

  /**
   * Access the Json config manager. This class manages reads and writes from the nicknames.json file.
   *
   * @return JsonConfig.
   */
  public @NotNull JsonConfig jsonConfig() {
    return this.jsonConfig;
  }

  /**
   * Get the map that stores unique ids keyed to nicknames.
   *
   * @return NickMap.
   */
  public @NotNull Map<UUID, Component> getNickMap() {
    return this.nickMap;
  }

  /**
   * Check whether there is a nickname stored for a unique id.
   *
   * @param uuid The unique id.
   * @return True if there is a nickname stored.
   * @deprecated for removal - this method will take a while to return when using SQL storage
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval
  public boolean hasNick(@NotNull UUID uuid) {
    try {
      Nicks.logging().debug("hasNick: " + storage.hasNick(uuid).get());
      return storage.hasNick(uuid).get();
    } catch (final InterruptedException | ExecutionException ex) {
      ex.printStackTrace();
      return false;
    }
  }

  /**
   * Get a nickname from a player's display name.
   *
   * @param player The player.
   * @return Nickname/Display name.
   */
  public @NotNull Component getDisplayName(@NotNull Player player) {
    return software.getNick(player).colorIfAbsent(config.DEFAULT_USERNAME_COLOR);
  }

  /**
   * Get a nickname from storage from a unique id.
   *
   * @param uuid Unique id.
   * @return Nickname if it exists.
   * @deprecated for removal - this method will take a while to return when using SQL storage
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval
  public Component getStoredNick(@NotNull UUID uuid) {
    try {
      Nicks.logging().debug("storedNick: " + storage.getNick(uuid).get());
      return storage.getNick(uuid).get();
    } catch (final InterruptedException | ExecutionException ex) {
      ex.printStackTrace();
      return Component.empty();
    }
  }

  /**
   * Set a user's nickname using an online {@link Player}.
   * This will immediately be saved to Json.
   *
   * @param player Online player.
   * @param nick Player's new nickname.
   */
  public void setNick(@NotNull Player player, @NotNull Component nick) {
    Nicks.logging().debug("Setting " + player.getName() + "'s nickname to \n" +
        new GsonBuilder().setPrettyPrinting().create().toJson(
            GsonComponentSerializer.gson().serializeToTree(nick)
        ) + "\n on platform " + software.softwareName()
    );
    software.setNick(player, nick);
    hooks.setEssNick(player, nick);
  }

  /**
   * Remove a nickname from the map and from Json storage.
   * It will be asynchronously removed from the file.
   *
   * @param player The player whose nickname to remove.
   */
  public void removeNick(@NotNull Player player) {
    software.removeNick(player);
    hooks.setEssNick(player, Component.text(player.getName()));
  }

  /**
   * Check if the plugin has a new update on Spigot.
   *
   * @return Whether there's an update.
   */
  public boolean hasUpdate() {
    return this.updateChecker.isBehindSpigot();
  }
}
