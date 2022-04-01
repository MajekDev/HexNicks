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
package dev.majek.hexnicks.hook;

import dev.majek.hexnicks.HexNicks;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handles PlaceholderAPI hook methods.
 */
class PapiHook extends PlaceholderExpansion {

  private final JavaPlugin plugin;

  public PapiHook(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean canRegister() {
    return true;
  }

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public @NotNull String getAuthor() {
    return plugin.getDescription().getAuthors().get(0);
  }

  @Override
  public @NotNull String getIdentifier() {
    return plugin.getDescription().getName().toLowerCase();
  }

  @Override
  public @NotNull String getVersion() {
    return plugin.getDescription().getVersion();
  }

  @Override
  @SuppressWarnings("all")
  public String onRequest(OfflinePlayer player, @NotNull String identifier) {
    if (identifier.equalsIgnoreCase("nick")) {
      return getNick(player);
    } else if (identifier.equalsIgnoreCase("nick_raw")) {
      Component nick = null;
      try {
        nick = HexNicks.storage().getNick(player.getUniqueId()).get();
      } catch (InterruptedException | ExecutionException e) {
        HexNicks.logging().error("Error retrieving nickname for Papi: ");
        e.printStackTrace();
      }

      if (nick == null) {
        return player.getName();
      } else {
        return PlainTextComponentSerializer.plainText().serialize(nick);
      }
    } else if (identifier.equalsIgnoreCase("nick_hex")) {
      Component nick = null;
      try {
        nick = HexNicks.storage().getNick(player.getUniqueId()).get();
      } catch (InterruptedException | ExecutionException e) {
        HexNicks.logging().error("Error retrieving nickname for Papi: ");
        e.printStackTrace();
      }

      if (nick == null) {
        return player.getName();
      } else {
        String legacy = LegacyComponentSerializer.builder().hexColors().character('&').build().serialize(nick);
        final Pattern pattern = Pattern.compile("&#([0-9a-fA-F]{6})");
        final Matcher matcher = pattern.matcher(legacy);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
          final StringBuilder replacement = new StringBuilder(7).append("#");
          for (final char character : matcher.group(1).toCharArray()) {
            replacement.append(character);
          }
          matcher.appendReplacement(sb, replacement.toString());
        }
        matcher.appendTail(sb);
        return sb.toString();
      }
    } else if (identifier.equalsIgnoreCase("nick_mm")) {
      Component nick = null;
      try {
        nick = HexNicks.storage().getNick(player.getUniqueId()).get();
      } catch (InterruptedException | ExecutionException e) {
        HexNicks.logging().error("Error retrieving nickname for Papi: ");
        e.printStackTrace();
      }

      if (nick == null) {
        return player.getName();
      } else {
        return MiniMessage.miniMessage().serialize(nick);
      }
    }
    return null;
  }

  @SuppressWarnings("all")
  private @Nullable String getNick(final @NotNull OfflinePlayer player) {
    try {
      Component nick = HexNicks.storage().getNick(player.getUniqueId()).get();
      if (nick == null) {
        return LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build()
            .serialize(Component.text(player.getName()).colorIfAbsent(HexNicks.config().DEFAULT_USERNAME_COLOR));
      } else {
        return LegacyComponentSerializer.builder().hexColors()
            .useUnusualXRepeatedCharacterHexFormat().build().serialize(nick);
      }
    } catch (InterruptedException | ExecutionException ex) {
      HexNicks.logging().error("Error retrieving nickname for Papi: ");
      ex.printStackTrace();
      return null;
    }
  }

  /**
   * Apply PAPI placeholders.
   *
   * @param player The player to set placeholders for.
   * @param message The message to set placeholders in.
   * @return Formatted message.
   */
  public static String applyPlaceholders(Player player, String message) {
    return PlaceholderAPI.setPlaceholders(player, message);
  }
}
