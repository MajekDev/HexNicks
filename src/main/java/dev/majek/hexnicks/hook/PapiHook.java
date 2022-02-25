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

import dev.majek.hexnicks.Nicks;
import java.util.concurrent.ExecutionException;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

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
      try {
        Component nick = Nicks.storage().getNick(player.getUniqueId()).get();
        if (nick == null) {
          return LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build()
              .serialize(Component.text(player.getName()).colorIfAbsent(Nicks.config().DEFAULT_USERNAME_COLOR));
        } else {
          return LegacyComponentSerializer.builder().hexColors()
              .useUnusualXRepeatedCharacterHexFormat().build().serialize(nick);
        }
      } catch (InterruptedException | ExecutionException ex) {
        Nicks.error("Error retrieving nickname for Papi: ");
        ex.printStackTrace();
      }
    }
    return null;
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
