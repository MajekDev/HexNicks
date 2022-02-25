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
package dev.majek.hexnicks.api;

import dev.majek.hexnicks.Nicks;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handles the Nicks API. Contains some useful utility methods.
 */
public class NicksApi {

  /**
   * Shortcut for calling a Bukkit event.
   *
   * @param event The event to call.
   * @since 1.0.0
   */
  public void callEvent(@NotNull Event event) {
    Nicks.core().getServer().getPluginManager().callEvent(event);
    Nicks.debug("Called event " + event.getEventName());
  }

  /**
   * Get the {@link OfflinePlayer} a nickname belongs to from the nickname string.
   *
   * @param nickname The nickname. If you want to call this with a {@link Component} as the param,
   *                 use {@link PlainTextComponentSerializer} to get a string.
   * @return The {@link OfflinePlayer} if found.
   * @since 1.0.0
   */
  @Nullable
  public OfflinePlayer playerFromNick(@NotNull String nickname) {
    Iterator<Map.Entry<UUID, Component>> iterator = Nicks.core().getNickMap()
        .entrySet().stream().iterator();
    while (iterator.hasNext()) {
      Map.Entry<UUID, Component> next = iterator.next();
      if (PlainTextComponentSerializer.plainText().serialize(next.getValue())
          .equalsIgnoreCase(nickname)) {
        return Bukkit.getOfflinePlayer(next.getKey());
      }
    }
    return null;
  }

  /**
   * Get a nickname from a player's unique id.
   *
   * @param uuid The unique id.
   * @return Nickname if it exists.
   * @deprecated for removal - this method will take a while to return when using SQL storage
   * @see #getStoredNick(UUID)
   */
  @Nullable
  @Deprecated
  @ApiStatus.ScheduledForRemoval
  public Component getNick(@NotNull UUID uuid) {
    return Nicks.core().getStoredNick(uuid);
  }

  /**
   * Get a nickname from an online {@link Player}.
   *
   * @param player Player.
   * @return Nickname if it exists.
   * @deprecated for removal - this method will take a while to return when using SQL storage
   * @see #getStoredNick(Player)
   */
  @Nullable
  @Deprecated
  @ApiStatus.ScheduledForRemoval
  public Component getNick(@NotNull Player player) {
    return Nicks.core().getStoredNick(player.getUniqueId());
  }

  /**
   * Get a nickname from an {@link OfflinePlayer}.
   *
   * @param player OfflinePlayer.
   * @return Nickname if it exists.
   * @deprecated for removal - this method will take a while to return when using SQL storage
   * @see #getStoredNick(OfflinePlayer)
   */
  @Nullable
  @Deprecated
  @ApiStatus.ScheduledForRemoval
  public Component getNick(@NotNull OfflinePlayer player) {
    return Nicks.core().getStoredNick(player.getUniqueId());
  }

  /**
   * Get a nickname from a player's unique id.
   *
   * @param uuid The unique id.
   * @return Nickname if it exists.
   */
  public @Nullable CompletableFuture<Component> getStoredNick(@NotNull UUID uuid) {
    return Nicks.storage().getNick(uuid);
  }

  /**
   * Get a nickname from an online {@link Player}.
   *
   * @param player Player.
   * @return Nickname if it exists.
   */
  public @Nullable CompletableFuture<Component> getStoredNick(@NotNull Player player) {
    return Nicks.storage().getNick(player.getUniqueId());
  }

  /**
   * Get a nickname from an {@link OfflinePlayer}.
   *
   * @param player OfflinePlayer.
   * @return Nickname if it exists.
   */
  public @Nullable CompletableFuture<Component> getStoredNick(@NotNull OfflinePlayer player) {
    return Nicks.storage().getNick(player.getUniqueId());
  }
}
