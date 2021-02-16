package dev.majek.hexnicks;

import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    FileConfiguration c = HexNicks.instance.getConfig();
    private final String host = c.getString("host");
    private final String port = c.getString("port");
    private final String database = c.getString("database");
    private final String username = c.getString("username");
    private final String password = c.getString("password");
    private final boolean useSSL = c.getBoolean("use-ssl");

    private Connection connection;

    public boolean isConnected() {
        return (connection != null);
    }

    public void connect() throws ClassNotFoundException, SQLException {
        if (!isConnected()) {
            connection = DriverManager.getConnection("jdbc:mysql://" +
                            host + ":" + port + "/" + database + "?useSSL=" + useSSL,
                    username, password);
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
