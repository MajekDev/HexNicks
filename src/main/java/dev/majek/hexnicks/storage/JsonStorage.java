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

import com.google.gson.JsonParser;
import dev.majek.hexnicks.Nicks;
import java.io.IOException;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handles Json storage for nicknames.
 */
public class JsonStorage implements StorageMethod {

  @Override
  public boolean hasNick(@NotNull UUID uuid) {
    return Nicks.core().getNickMap().containsKey(uuid);
  }

  @Override
  @SuppressWarnings("ConstantConditions")
  public Component getNick(@NotNull UUID uuid) {
    return hasNick(uuid) ? Nicks.core().getNickMap().get(uuid)
        : Component.text(Bukkit.getOfflinePlayer(uuid).getName());
  }

  @Override
  public void removeNick(@NotNull UUID uuid) {
    Nicks.core().getNickMap().remove(uuid);
    Bukkit.getScheduler().runTaskAsynchronously(Nicks.core(), () -> {
      try {
        Nicks.core().jsonConfig().removeFromJsonObject(uuid.toString());
        Nicks.debug("Removed nickname from user " + uuid + " from json.");
      } catch (IOException e) {
        Nicks.error("Error removing nickname from file \nUUID: " + uuid);
        e.printStackTrace();
      }
    });
  }

  @Override
  public void saveNick(@NotNull Player player) {
    Bukkit.getScheduler().runTaskAsynchronously(Nicks.core(), () -> {
      try {
        Nicks.core().jsonConfig().putInJsonObject(player.getUniqueId().toString(),
            JsonParser.parseString(GsonComponentSerializer.gson().serialize(Nicks.software().getNick(player))));
        Nicks.debug("Saved nickname from user " + player.getName() + " to json.");
      } catch (IOException e) {
        Nicks.error("Error saving nickname to file \nUUID: " + player.getUniqueId());
        e.printStackTrace();
      }
    });
  }

  @Override
  public void updateNicks() {
    // Nothing needed for json storage
  }
}