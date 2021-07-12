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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles general utility methods.
 */
public class NicksUtils {

  /**
   * Convert a string with legacy codes into a string with MiniMessage tags.
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
   * Convert a string with MiniMessage tags into a string with legacy codes..
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
    text = applyLegacyColors(text);
    //text = MiniMessage.get().stripTokens(text);
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
    return MiniMessage.get().parse(Nicks.hooks().applyPlaceHolders(player, Nicks.core()
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
    return MiniMessage.get().parse(Nicks.core().getConfig().getString(path, def));
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
}