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
package dev.majek.hexnicks.util;

import dev.majek.hexnicks.Nicks;
import dev.majek.hexnicks.config.NicksMessages;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
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
   * Convert a string with legacy codes into a string with {@link MiniMessage} tags.
   *
   * @param text Text to search
   * @return String with MiniMessage tags.
   */
  public String legacyToMini(String text) {
    text = text.replace("&0", "<black>");
    text = text.replace("&1", "<dark_blue>");
    text = text.replace("&2", "<dark_green>");
    text = text.replace("&3", "<dark_aqua>");
    text = text.replace("&4", "<dark_red>");
    text = text.replace("&5", "<dark_purple>");
    text = text.replace("&6", "<gold>");
    text = text.replace("&7", "<gray>");
    text = text.replace("&8", "<dark_gray>");
    text = text.replace("&9", "<blue>");
    text = text.replace("&a", "<green>");
    text = text.replace("&b", "<aqua>");
    text = text.replace("&c", "<red>");
    text = text.replace("&d", "<light_purple>");
    text = text.replace("&e", "<yellow>");
    text = text.replace("&f", "<white>");
    text = text.replace("&m", "<underlined>");
    text = text.replace("&m", "<strikethrough>");
    text = text.replace("&k", "<obfuscated>");
    text = text.replace("&o", "<italic>");
    text = text.replace("&l", "<bold>");
    text = text.replace("&r", "<reset>");
    return text;
  }

  /**
   * Convert a string with {@link MiniMessage} tags into a string with legacy codes.
   *
   * @param text Text to search
   * @return String with legacy codes.
   */
  public String miniToLegacy(String text) {
    text = text.replace("<black>", "&0");
    text = text.replace("<dark_blue>", "&1");
    text = text.replace("<dark_green>", "&2");
    text = text.replace("<dark_aqua>", "&3");
    text = text.replace("<dark_red>", "&4");
    text = text.replace("<dark_purple>", "&5");
    text = text.replace("<gold>", "&6");
    text = text.replace("<gray>", "&7");
    text = text.replace("<dark_gray>", "&8");
    text = text.replace("<blue>", "&9");
    text = text.replace("<green>", "&a");
    text = text.replace("<aqua>", "&b");
    text = text.replace("<red>", "&c");
    text = text.replace("<light_purple>", "&d");
    text = text.replace("<yellow>", "&e");
    text = text.replace("<white>", "&f");
    text = text.replace("<underlined>", "&m");
    text = text.replace("<strikethrough>", "&m");
    text = text.replace("<obfuscated>", "&k");
    text = text.replace("<italic>", "&o");
    text = text.replace("<bold>", "&l");
    text = text.replace("<reset>", "&r");
    text = MiniMessage.miniMessage().stripTokens(text);
    text = applyLegacyColors(text);
    return text;
  }

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
    return MiniMessage.miniMessage().parse(Nicks.hooks().applyPlaceHolders(player, Nicks.core()
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
    return MiniMessage.miniMessage().parse(Nicks.core().getConfig().getString(path, def));
  }

  /** Pattern matching "nicer" legacy hex chat color codes - &#rrggbb */
  private static final Pattern NICER_HEX_COLOR_PATTERN = Pattern.compile("&#([0-9a-fA-F]{6})");

  /**
   * Translates color codes in the given input string.
   *
   * @param string the string to "colorize"
   * @return the colorized string
   */
  public String applyLegacyColors(String string) {
    if (string == null)
      return "null";

    // Convert from the '&#rrggbb' hex color format to the '&x&r&r&g&g&b&b' one used by Bukkit.
    Matcher matcher = NICER_HEX_COLOR_PATTERN.matcher(string);
    StringBuilder sb = new StringBuilder();

    while (matcher.find()) {
      StringBuilder replacement = new StringBuilder(14).append("&x");
      for (char character : matcher.group(1).toCharArray())
        replacement.append('&').append(character);
      matcher.appendReplacement(sb, replacement.toString());
    }
    matcher.appendTail(sb);

    // Translate from '&' to '§' (section symbol)
    return ChatColor.translateAlternateColorCodes('&', sb.toString());
  }

  /**
   * Check if a nickname is taken and prevent it.
   *
   * @param nickname the nickname to check
   * @param player the player trying to set the nickname
   * @return true if the nickname was a duplicate and the message was sent
   */
  public boolean preventDuplicates(@NotNull Component nickname, @NotNull CommandSender player) {
    if (Nicks.config().PREVENT_DUPLICATE_NICKS) {
      boolean taken = false;
      try {
        taken = Nicks.storage().nicknameExists(nickname, Nicks.config().PREVENT_DUPLICATE_NICKS_STRICT).get();
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
