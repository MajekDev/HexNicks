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
  public boolean hasNick(@NotNull UUID uuid) {
    try {
      PreparedStatement ps = Nicks.sql().getConnection()
          .prepareStatement("SELECT nickname FROM nicknameTable WHERE uniqueId=?");
      ps.setString(1, uuid.toString());
      ResultSet resultSet = ps.executeQuery();
      String nickname;
      if (resultSet.next()) {
        nickname = resultSet.getString("nickname");
        return nickname == null;
      } else {
        return false;
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
  }

  @Override
  @SuppressWarnings("ConstantConditions")
  public Component getNick(@NotNull UUID uuid) {
    try {
      PreparedStatement ps = Nicks.sql().getConnection()
          .prepareStatement("SELECT nickname FROM nicknameTable WHERE uniqueId=?");
      ps.setString(1, uuid.toString());
      ResultSet resultSet = ps.executeQuery();
      String nickname;
      if (resultSet.next()) {
        nickname = resultSet.getString("nickname");
        return GsonComponentSerializer.gson().deserialize(nickname);
      }else {insertPlayer(uuid);}
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return Component.text(Bukkit.getOfflinePlayer(uuid).getName());
  }

  @Override
  public void removeNick(@NotNull UUID uuid) {
    try {
      PreparedStatement ps = Nicks.sql().getConnection()
          .prepareStatement("DELETE FROM nicknameTable WHERE uniqueId=?");
      ps.setString(1, uuid.toString());
      ps.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void saveNick(@NotNull UUID uuid) {
    try {
      PreparedStatement ps = Nicks.sql().getConnection()
          .prepareStatement("UPDATE nicknameTable SET nickname=? WHERE uniqueId=?");
      ps.setString(1, GsonComponentSerializer.gson().serialize(getNick(uuid)));
      ps.setString(2, uuid.toString());
      ps.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void updateNicks() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      Nicks.software().setNick(player, getNick(player.getUniqueId()));
    }
  }
  public void insertPlayer(@NotNull UUID uuid){
    try {
      PreparedStatement ps = Nicks.sql().getConnection()
              .prepareStatement("INSERT INTO `nicknameTable` (`uniqueId`, `nickname`) VALUES (?, ?);");
      ps.setString(2, GsonComponentSerializer.gson().serialize(Component.text(Bukkit.getOfflinePlayer(uuid).getName())));
      ps.setString(1, uuid.toString());
      Nicks.log("nicksqldebug: "+ ps.toString());
      //System.out.println("nicksqldebug: "+ ps.toString());
      ps.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }
}
