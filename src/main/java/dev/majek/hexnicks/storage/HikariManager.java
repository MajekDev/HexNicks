/*
 * This file is part of HexNicks, licensed under the MIT License.
 *
 * Copyright (c) Majekdor
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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.majek.hexnicks.HexNicks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Handles the plugin's MySQL connections.
 */
public class HikariManager {

    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource dataSource;

    static {
        String host = HexNicks.core().getConfig().getString("host", "");
        String port = HexNicks.core().getConfig().getString("port", "");
        String database = HexNicks.core().getConfig().getString("database", "");
        String username = HexNicks.core().getConfig().getString("username", "");
        String password = HexNicks.core().getConfig().getString("password", "");
        boolean useSSL = HexNicks.core().getConfig().getBoolean("use-ssl", false);
        boolean autoReconnect = HexNicks.core().getConfig().getBoolean("auto-reconnect", false);
        long maxLifetime = HexNicks.core().getConfig().getLong("max-lifetime", 300000L);

        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + useSSL
                + "&autoReconnect=" + autoReconnect);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(20);
        config.setMaxLifetime(maxLifetime);
        dataSource = new HikariDataSource(config);
    }

    private HikariManager() {}

    /**
     * Get the current SQL connection.
     *
     * @return the sql connection
     * @throws SQLException if getting the connection fails
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Create the nickname table if it doesn't exist.
     *
     * @throws SQLException if creating the table fails
     */
    public static void createTable() throws SQLException {
        HexNicks.logging().debug("Creating sql database table...");
        try (Connection connection = HikariManager.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                    "nicknameTable (uniqueId VARCHAR(100),nickname VARCHAR(10000),PRIMARY KEY (uniqueId))");
            ps.executeUpdate();
        } catch (final SQLException ex) {
            HexNicks.logging().error("Error creating nickname table", ex);
            throw new SQLException();
        }
    }
}
