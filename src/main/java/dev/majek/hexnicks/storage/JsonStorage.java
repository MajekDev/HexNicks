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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.majek.hexnicks.HexNicks;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handles Json storage for nicknames.
 */
public class JsonStorage implements StorageMethod {

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  @Override
  public CompletableFuture<Boolean> hasNick(@NotNull UUID uuid) {
    return CompletableFuture.supplyAsync(() -> HexNicks.core().getNickMap().containsKey(uuid));
  }

  @Override
  @SuppressWarnings("ConstantConditions")
  public CompletableFuture<Component> getNick(@NotNull UUID uuid) {
    return hasNick(uuid).thenApplyAsync(b -> b ? HexNicks.core().getNickMap().get(uuid)
        : Component.text(Bukkit.getOfflinePlayer(uuid).getName()));
  }

  @Override
  public void removeNick(@NotNull UUID uuid) {
    HexNicks.core().getNickMap().remove(uuid);
    try {
      JsonObject json = (JsonObject) JsonParser.parseReader(new FileReader(HexNicks.core().jsonFile()));
      json.remove(uuid.toString());
      final PrintWriter writer = new PrintWriter(HexNicks.core().jsonFile());
      writer.write(GSON.toJson(json));
      writer.flush();
      writer.close();
      HexNicks.logging().debug("Removed nickname from user " + uuid + " from json.");
    } catch (final IOException ex) {
      HexNicks.logging().error("Error removing nickname from file \nUUID: " + uuid, ex);
    }
  }

  @Override
  public synchronized void saveNick(@NotNull Player player, @NotNull Component nickname) {
    try {
      JsonObject json = (JsonObject) JsonParser.parseReader(new FileReader(HexNicks.core().jsonFile()));
      json.add(
          player.getUniqueId().toString(),
          GsonComponentSerializer.gson().serializeToTree(nickname)
      );
      final PrintWriter writer = new PrintWriter(HexNicks.core().jsonFile());
      writer.write(GSON.toJson(json));
      writer.flush();
      writer.close();
      HexNicks.logging().debug("Saved nickname from user " + player.getName() + " to json.");
    } catch (final IOException ex) {
      HexNicks.logging().error("Error saving nickname to file \nUUID: " + player.getUniqueId(), ex);
    }
  }

  @Override
  public CompletableFuture<Boolean> nicknameExists(@NotNull Component nickname, boolean strict, @NotNull Player player) {
    List<Component> taken = new ArrayList<>();
    for (Map.Entry<UUID, Component> entry : HexNicks.core().getNickMap().entrySet()) {
      if (!entry.getKey().equals(player.getUniqueId())) {
        taken.add(entry.getValue());
      }
    }
    taken.addAll(
        Arrays.stream(Bukkit.getOfflinePlayers())
            .filter(offlinePlayer -> !offlinePlayer.getUniqueId().equals(player.getUniqueId()))
            .map(OfflinePlayer::getName)
            .filter(Objects::nonNull)
            .map(Component::text)
            .toList()
    );
    if (strict) {
      for (Component value : taken) {
        if (PlainTextComponentSerializer.plainText().serialize(value)
            .equalsIgnoreCase(PlainTextComponentSerializer.plainText().serialize(nickname))) {
          return CompletableFuture.supplyAsync(() -> true);
        }
      }
    } else {
      return CompletableFuture.supplyAsync(() -> taken.contains(nickname));
    }
    return CompletableFuture.supplyAsync(() -> false);
  }
}
