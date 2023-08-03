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

import dev.majek.hexnicks.HexNicks;
import dev.majek.hexnicks.config.Messages;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.format.NamedTextColor.*;

/**
 * Handles general utility methods.
 */
public class MiscUtils {

  private MiscUtils() {}

  /**
   * Get a string from the config, apply placeholders from PlaceholderAPI,
   * and parse it into a Component with MiniMessage.
   *
   * @param path The path to the string.
   * @param def The default if the path returns null.
   * @param player The player for placeholders.
   * @return Formatted component.
   */
  public static Component configStringPlaceholders(String path, String def, Player player) {
    return MiniMessage.miniMessage().deserialize(HexNicks.hooks().applyPlaceHolders(player, HexNicks.core()
        .getConfig().getString(path, def)));
  }

  /**
   * Get a string from the config and parse it into a Component with MiniMessage.
   *
   * @param path The path to the string.
   * @param def The default if the path returns null.
   * @return Formatted component.
   */
  public static Component configString(String path, String def) {
    return MiniMessage.miniMessage().deserialize(HexNicks.core().getConfig().getString(path, def));
  }

  /**
   * Check if a nickname is taken and prevent it.
   *
   * @param nickname the nickname to check
   * @param player the player trying to set the nickname
   * @return true if the nickname was a duplicate and the message was sent
   */
  public static boolean preventDuplicates(@NotNull Component nickname, @NotNull Player player) {
    if (HexNicks.config().PREVENT_DUPLICATE_NICKS) {
      final AtomicBoolean taken = new AtomicBoolean(false);
      HexNicks.storage().nicknameExists(nickname, HexNicks.config().PREVENT_DUPLICATE_NICKS_STRICT, player).whenComplete((bool, error) -> {
        if (error != null) {
          HexNicks.logging().error("Error checking if nickname exists.", error);
        } else {
          taken.set(bool);
        }
      });
      if (taken.get()) {
        Messages.NICKNAME_TAKEN.send(player);
        return true;
      }
    }
    return false;
  }

  /**
   * Check whether the given nickname is blocked by config settings.
   *
   * @param plainTextNick the provided nickname in plain text
   * @return whether the nickname is blocked.
   */
  public static boolean isBlocked(@NotNull String plainTextNick) {
    for (String blockedNick : HexNicks.config().BLOCKED_NICKNAMES) {
      if (isValidRegex(blockedNick) && plainTextNick.matches(blockedNick)) {
        return true;
      } else if (blockedNick.equalsIgnoreCase(plainTextNick)) {
          return true;
      }
    }
    return false;
  }

  private static boolean isValidRegex(@NotNull String string) {
    try {
      Pattern.compile(string);
      return true;
    } catch (PatternSyntaxException ignored) {
      return false;
    }
  }

  /**
   * Get the legacy color code from a named text color.
   *
   * @param color the named text color
   * @return legacy color code
   */
  public static @Nullable Character legacyCodeFromNamed(@NotNull NamedTextColor color) {
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

  /**
   * Get a set of blocked colors for a command sender based on permissions.
   *
   * @param sender the sender
   * @return blocked colors
   */
  public static @NotNull Set<@NotNull NamedTextColor> blockedColors(final @NotNull CommandSender sender) {
    final Set<NamedTextColor> set = new HashSet<>();
    for (final NamedTextColor color : NamedTextColor.NAMES.values()) {
      if (!sender.hasPermission("hexnicks.color." + color.toString().toLowerCase(Locale.ROOT))) {
        set.add(color);
      }
    }
    return set;
  }

  /**
   * Get a set of blocked decorations for a command sender based on permissions.
   *
   * @param sender the sender
   * @return blocked decorations
   */
  public static @NotNull Set<@NotNull TextDecoration> blockedDecorations(final @NotNull CommandSender sender) {
    final Set<TextDecoration> set = new HashSet<>();
    for (final TextDecoration decoration : TextDecoration.values()) {
      if (!sender.hasPermission("hexnicks.decoration." + decoration.toString().toLowerCase(Locale.ROOT))) {
        set.add(decoration);
      }
    }
    return set;
  }

  /**
   * Send a http request and get the response body.
   *
   * @param request the request
   * @return the response body
   */
  public static @NotNull String sendRequestAndGetResponse(final @NotNull HttpRequest request) {
    HttpResponse<String> response = null;
    try {
      response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException ex) {
      ex.printStackTrace();
    }
    if (response == null) {
      throw new RuntimeException("Error getting response from ByteBin");
    }
    return response.body();
  }

  /**
   * Send a message to a user provided the message isn't empty.
   *
   * @param recipient the recipient of the message
   * @param message the message to send
   */
  public static void sendMessage(final @NotNull CommandSender recipient, final @NotNull Component message) {
    if (!PlainTextComponentSerializer.plainText().serialize(message).isEmpty()) {
      recipient.sendMessage(message);
    }
  }

  /**
   * Announce a message to the server provided the message isn't empty.
   *
   * @param message the message to send
   */
  public static void announceMessage(final @NotNull Component message) {
    if (!PlainTextComponentSerializer.plainText().serialize(message).isEmpty()) {
      for (Player player : Bukkit.getOnlinePlayers()) {
        player.sendMessage(message);
      }
    }
  }
}
