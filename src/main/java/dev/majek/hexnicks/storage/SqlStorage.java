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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handles Sql storage for nicknames.
 */
public class SqlStorage implements StorageMethod {

  @Override
  public CompletableFuture<Boolean> hasNick(@NotNull UUID uuid) {
    HexNicks.logging().debug("Firing SqlStorage#hasNick for uuid: " + uuid);
    return CompletableFuture.supplyAsync(() -> {
          try (Connection connection = HikariManager.getConnection()) {
            PreparedStatement ps = connection
                    .prepareStatement("SELECT nickname FROM nicknameTable WHERE uniqueId=?");
            ps.setString(1, uuid.toString());
            ResultSet resultSet = ps.executeQuery();
            String nickname;
            if (resultSet.next()) {
              nickname = resultSet.getString("nickname");
              return nickname != null;
            } else {
              HexNicks.logging().debug("No nickname found in database for uuid: " + uuid);
              return false;
            }
          } catch (SQLException ex) {
            HexNicks.logging().error("Error with SqlStorage#hasNick", ex);
            ex.printStackTrace();
            return false;
          }
        });
  }

  @Override
  @SuppressWarnings("ConstantConditions")
  public CompletableFuture<Component> getNick(@NotNull UUID uuid) {
    HexNicks.logging().debug("Firing SqlStorage#getNick for uuid: " + uuid);
    return CompletableFuture.supplyAsync(() -> {
      try (Connection connection = HikariManager.getConnection()){
        PreparedStatement ps = connection.prepareStatement("SELECT nickname FROM nicknameTable WHERE uniqueId=?");
        ps.setString(1, uuid.toString());
        ResultSet resultSet = ps.executeQuery();
        String nickname;
        if (resultSet.next()) {
          nickname = resultSet.getString("nickname");
          HexNicks.logging().debug("Nickname found for uuid: " + uuid);
          return GsonComponentSerializer.gson().deserialize(nickname);
        } else {
          HexNicks.logging().debug("No nickname found in database for uuid: " + uuid);
        }
      } catch (SQLException ex) {
        HexNicks.logging().error("Error with SqlStorage#getNick", ex);
        ex.printStackTrace();
      }
      HexNicks.logging().debug("Returning player's username as nickname");
      return Component.text(Bukkit.getOfflinePlayer(uuid).getName());
    });
  }

  @Override
  public void removeNick(@NotNull UUID uuid) {
    HexNicks.logging().debug("Firing SqlStorage#removeNick...");
    Bukkit.getScheduler().runTaskAsynchronously(HexNicks.core(), () -> {
      try (Connection connection = HikariManager.getConnection()) {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM nicknameTable WHERE uniqueId=?");
        ps.setString(1, uuid.toString());
        ps.executeUpdate();
      } catch (SQLException ex) {
        HexNicks.logging().error("Error with SqlStorage#removeNick", ex);
        ex.printStackTrace();
      }
    });
  }

  @Override
  public void saveNick(@NotNull Player player, @NotNull Component nickname) {
    HexNicks.logging().debug("Firing SqlStorage#saveNick...");
    Bukkit.getScheduler().runTaskAsynchronously(HexNicks.core(), () ->
        hasNick(player.getUniqueId()).whenCompleteAsync((hasNick, throwable) -> {
          try (Connection connection = HikariManager.getConnection()) {
            PreparedStatement update;
            if (hasNick) {
              HexNicks.logging().debug("Has nick already, updating table...");
              update = connection.prepareStatement("UPDATE nicknameTable SET nickname=? WHERE uniqueId=?");
              update.setString(1, GsonComponentSerializer.gson().serialize(nickname));
              update.setString(2, player.getUniqueId().toString());
            } else {
              HexNicks.logging().debug("No nick found, inserting into table...");
              update = connection
                      .prepareStatement("INSERT INTO `nicknameTable` (`uniqueId`, `nickname`) VALUES (?, ?);");
              update.setString(1, player.getUniqueId().toString());
              update.setString(2, GsonComponentSerializer.gson().serialize(nickname));
            }
            update.executeUpdate();
          } catch (SQLException ex) {
            HexNicks.logging().error("Error with SqlStorage#saveNick", ex);
            ex.printStackTrace();
          }
        })
    );
  }

  @Override
  public CompletableFuture<Boolean> nicknameExists(@NotNull Component nickname, boolean strict, @NotNull Player player) {
    HexNicks.logging().debug("Firing SqlStorage#nicknameExists...");
    return CompletableFuture.supplyAsync(() -> {
      try (Connection connection = HikariManager.getConnection()) {
        // Add all player names except the player setting the nickname
        List<Component> taken = Arrays.stream(Bukkit.getOfflinePlayers())
            .filter(offlinePlayer -> !offlinePlayer.getUniqueId().equals(player.getUniqueId()))
            .map(OfflinePlayer::getName)
            .filter(Objects::nonNull)
            .map(Component::text).collect(Collectors.toList());

        // Add all stored nicknames
        PreparedStatement ps = connection.prepareStatement("SELECT nickname FROM nicknameTable WHERE uniqueId !=?");
        ps.setString(1, player.getUniqueId().toString());
        ResultSet resultSet = ps.executeQuery();
        while (resultSet.next()) {
          taken.add(GsonComponentSerializer.gson().deserialize(resultSet.getString("nickname")));
        }
        if (strict) {
          for (Component value : taken) {
            if (PlainTextComponentSerializer.plainText().serialize(value)
                .equalsIgnoreCase(PlainTextComponentSerializer.plainText().serialize(nickname))) {
              return true;
            }
          }
        } else {
          return taken.contains(nickname);
        }
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
      return false;
    });
  }
}
