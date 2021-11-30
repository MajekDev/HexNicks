/*
 * This file is part of HexNicks, licensed under the MIT License.
 *
 * Copyright (c) 2020-2021 Majekdor
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

import dev.majek.hexnicks.Nicks;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handles Sql storage for nicknames.
 */
public class SqlStorage implements StorageMethod {

  @Override
  public CompletableFuture<Boolean> hasNick(@NotNull UUID uuid) {
    return CompletableFuture.supplyAsync(() -> {
          try {
            PreparedStatement ps = Nicks.sql().getConnection()
                .prepareStatement("SELECT nickname FROM nicknameTable WHERE uniqueId=?");
            ps.setString(1, uuid.toString());
            ResultSet resultSet = ps.executeQuery();
            String nickname;
            if (resultSet.next()) {
              nickname = resultSet.getString("nickname");
              return nickname != null;
            } else {
              return false;
            }
          } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
          }
        });
  }

  @Override
  @SuppressWarnings("ConstantConditions")
  public CompletableFuture<Component> getNick(@NotNull UUID uuid) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        PreparedStatement ps = Nicks.sql().getConnection()
            .prepareStatement("SELECT nickname FROM nicknameTable WHERE uniqueId=?");
        ps.setString(1, uuid.toString());
        ResultSet resultSet = ps.executeQuery();
        String nickname;
        if (resultSet.next()) {
          nickname = resultSet.getString("nickname");
          return GsonComponentSerializer.gson().deserialize(nickname);
        }
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
      return Component.text(Bukkit.getOfflinePlayer(uuid).getName());
    });
  }

  @Override
  public void removeNick(@NotNull UUID uuid) {
    Bukkit.getScheduler().runTaskAsynchronously(Nicks.core(), () -> {
      try {
        PreparedStatement ps = Nicks.sql().getConnection()
            .prepareStatement("DELETE FROM nicknameTable WHERE uniqueId=?");
        ps.setString(1, uuid.toString());
        ps.executeUpdate();
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
    });
  }

  @Override
  public void saveNick(@NotNull Player player) {
    Bukkit.getScheduler().runTaskAsynchronously(Nicks.core(), () -> {
      hasNick(player.getUniqueId()).whenCompleteAsync((aBoolean, throwable) -> {
        try {
          PreparedStatement update;
          if (aBoolean) {
            update = Nicks.sql().getConnection()
                .prepareStatement("UPDATE nicknameTable SET nickname=? WHERE uniqueId=?");
            update.setString(1, GsonComponentSerializer.gson().serialize(Nicks.software().getNick(player)));
            update.setString(2, player.getUniqueId().toString());
          } else {
            update = Nicks.sql().getConnection()
                .prepareStatement("INSERT INTO `nicknameTable` (`uniqueId`, `nickname`) VALUES (?, ?);");
            update.setString(1, player.getUniqueId().toString());
            update.setString(2, GsonComponentSerializer.gson().serialize(Nicks.software().getNick(player)));
          }
          update.executeUpdate();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      });
    });
  }
}