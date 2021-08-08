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

package dev.majek.hexnicks.event;

import dev.majek.hexnicks.Nicks;
import dev.majek.hexnicks.storage.SqlStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * <p>Handles the player join event.</p>
 * <p>Sets the player displayname if they have a nickname in the map.</p>
 */
public class PlayerJoin implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerJoin(PlayerJoinEvent event) {
    Nicks.storage().updateNicks();
    if (Nicks.core().hasNick(event.getPlayer().getUniqueId())) {
      Nicks.core().setNick(event.getPlayer(), Nicks.core().getStoredNick(event.getPlayer().getUniqueId()));
    } else if (Nicks.storage() instanceof SqlStorage) {
      try {
        PreparedStatement ps = Nicks.sql().getConnection()
                .prepareStatement("INSERT INTO `nicknameTable` (`uniqueId`, `nickname`) VALUES (?, ?);");
        ps.setString(2, GsonComponentSerializer.gson().serialize(Component.text(event.getPlayer().getName())));
        ps.setString(1, event.getPlayer().getUniqueId().toString());
        ps.executeUpdate();
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
    }
  }
}