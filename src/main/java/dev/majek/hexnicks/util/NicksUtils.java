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
package dev.majek.hexnicks.util;

import dev.majek.hexnicks.Nicks;
import dev.majek.hexnicks.config.NicksMessages;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.format.NamedTextColor.*;

/**
 * Handles general utility methods.
 */
public class NicksUtils {

  /**
   * Get a string from the config, apply placeholders from PlaceholderAPI,
   * and parse it into a Component with MiniMessage.
   *
   * @param path The path to the string.
   * @param def The default if the path returns null.
   * @param player The player for placeholders.
   * @return Formatted component.
   */
  public Component configStringPlaceholders(String path, String def, Player player) {
    return MiniMessage.miniMessage().deserialize(Nicks.hooks().applyPlaceHolders(player, Nicks.core()
        .getConfig().getString(path, def)));
  }

  /**
   * Get a string from the config and parse it into a Component with MiniMessage.
   *
   * @param path The path to the string.
   * @param def The default if the path returns null.
   * @return Formatted component.
   */
  public Component configString(String path, String def) {
    return MiniMessage.miniMessage().deserialize(Nicks.core().getConfig().getString(path, def));
  }

  /**
   * Check if a nickname is taken and prevent it.
   *
   * @param nickname the nickname to check
   * @param player the player trying to set the nickname
   * @return true if the nickname was a duplicate and the message was sent
   */
  public boolean preventDuplicates(@NotNull Component nickname, @NotNull Player player) {
    if (Nicks.config().PREVENT_DUPLICATE_NICKS) {
      boolean taken = false;
      try {
        taken = Nicks.storage().nicknameExists(nickname, Nicks.config().PREVENT_DUPLICATE_NICKS_STRICT, player).get();
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
      if (taken) {
        NicksMessages.NICKNAME_TAKEN.send(player);
        return true;
      }
    }
    return false;
  }

  public @Nullable Character legacyCodeFromNamed(@NotNull NamedTextColor color) {
    if (BLACK.equals(color)) {
      return '0';
    } else if (DARK_BLUE.equals(color)) {
      return '1';
    } else if (DARK_GREEN.equals(color)) {
      return '2';
    } else if (DARK_AQUA.equals(color)) {
      return '3';
    } else if (DARK_RED.equals(color)) {
      return '4';
    } else if (DARK_PURPLE.equals(color)) {
      return '5';
    } else if (GOLD.equals(color)) {
      return '6';
    } else if (GRAY.equals(color)) {
      return '7';
    } else if (DARK_GRAY.equals(color)) {
      return '8';
    } else if (BLUE.equals(color)) {
      return '9';
    } else if (GREEN.equals(color)) {
      return 'a';
    } else if (AQUA.equals(color)) {
      return 'b';
    } else if (RED.equals(color)) {
      return 'c';
    } else if (LIGHT_PURPLE.equals(color)) {
      return 'd';
    } else if (YELLOW.equals(color)) {
      return 'e';
    } else if (WHITE.equals(color)) {
      return 'f';
    }
    return null;
  }

  public Set<NamedTextColor> blockedColors(@NotNull CommandSender sender) {
    Set<NamedTextColor> set = new HashSet<>();
    for (NamedTextColor color : NamedTextColor.NAMES.values()) {
      if (!sender.hasPermission("hexnicks.color." + color.toString().toLowerCase(Locale.ROOT))) {
        set.add(color);
      }
    }
    return set;
  }
}
