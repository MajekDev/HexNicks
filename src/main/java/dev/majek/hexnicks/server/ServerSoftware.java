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
package dev.majek.hexnicks.server;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * Handles different server software.
 */
public interface ServerSoftware extends Listener {

  /**
   * Fetch the player's nickname, which is their current display name.
   *
   * @param player The player.
   * @return Their nickname as a Component.
   */
  @NotNull Component getNick(@NotNull Player player);

  /**
   * Set a player's display name to their nickname.
   *
   * @param player The player whose display name should be modified.
   * @param nickname The nickname to set.
   */
  void setNick(@NotNull Player player, @NotNull Component nickname);

  /**
   * Remove a player's nickname.
   *
   * @param player The player.
   */
  void removeNick(@NotNull Player player);

  /**
   * Send a command sender a message.
   *
   * @param sender The message recipient.
   * @param message The message.
   */
  void sendMessage(@NotNull CommandSender sender, @NotNull Component message);

  /**
   * Get the name of the software implementation.
   *
   * @return name
   */
  @NotNull String softwareName();
}
