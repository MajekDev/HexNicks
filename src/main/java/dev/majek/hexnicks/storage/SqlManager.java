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
package dev.majek.hexnicks.storage;

import dev.majek.hexnicks.HexNicks;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.bukkit.Bukkit;

/**
 * Handles the plugin's MySQL connection.
 */
public class SqlManager {

  private final String host = HexNicks.core().getConfig().getString("host");
  private final String port = HexNicks.core().getConfig().getString("port");
  private final String database = HexNicks.core().getConfig().getString("database");
  private final String username = HexNicks.core().getConfig().getString("username");
  private final String password = HexNicks.core().getConfig().getString("password");
  private final boolean useSSL = HexNicks.core().getConfig().getBoolean("use-ssl");
  private final boolean autoReconnect = HexNicks.core().getConfig().getBoolean("auto-reconnect");
  private final int updateInterval = HexNicks.core().getConfig().getInt("update-interval", 300);

  private Connection connection;

  /**
   * Check if the plugin is connected to a database.
   *
   * @return True if connected.
   */
  public boolean isConnected() {
    return (connection != null);
  }

  /**
   * Try to connect to the database defined in the config file.
   *
   * @throws SQLException if there is an error connecting.
   */
  public void connect() throws SQLException {
    if (!isConnected()) {
      connection = DriverManager.getConnection("jdbc:mysql://" +
              host + ":" + port + "/" + database + "?useSSL=" + useSSL + "&autoReconnect=" + autoReconnect, username, password);
      HexNicks.logging().debug("Connecting to sql database '" + database + "'...");
    }
    Bukkit.getScheduler().scheduleSyncRepeatingTask(HexNicks.core(), () -> HexNicks.storage().updateNicks(), 200L, updateInterval * 20L);
  }

  /**
   * Disconnect from the database if connected.
   */
  public void disconnect() {
    if (isConnected()) {
      try {
        HexNicks.logging().debug("Disconnecting from sql database...");
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Get the connection to the database.
   *
   * @return The connection.
   */
  public Connection getConnection() {
    return connection;
  }

  /**
   * Create the MySQL table if it doesn't already exist.
   */
  public void createTable() {
    HexNicks.logging().debug("Creating sql database table...");
    PreparedStatement ps;
    try {
      ps = HexNicks.sql().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " +
          "nicknameTable (uniqueId VARCHAR(100),nickname VARCHAR(10000),PRIMARY KEY (uniqueId))");
      ps.executeUpdate();
    } catch (SQLException ex) {
      HexNicks.logging().error("Error creating table in database", ex);
      ex.printStackTrace();
    }
  }
}
