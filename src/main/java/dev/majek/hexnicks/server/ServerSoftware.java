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

import dev.majek.hexnicks.Nicks;
import dev.majek.hexnicks.util.MiniMessageWrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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

  /**
   * Instance of the {@link LegacyComponentSerializer} used by server implementations.
   */
  LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.builder().hexColors()
      .useUnusualXRepeatedCharacterHexFormat().character('§').build();

  /**
   * Format the chat for all server implementations.
   *
   * @param source the chatter
   * @param message the message
   * @return formatted chat
   */
  default @NotNull Component formatChat(final @NotNull Player source, final @NotNull String message) {
    final MiniMessageWrapper miniMessageWrapper = MiniMessageWrapper.builder()
        .gradients(source.hasPermission("hexnicks.color.gradient"))
        .hexColors(source.hasPermission("hexnicks.color.hex"))
        .legacyColors(Nicks.config().LEGACY_COLORS)
        .removeTextDecorations(Nicks.config().DISABLED_DECORATIONS.toArray(new TextDecoration[0]))
        .removeColors(Nicks.utils().blockedColors(source).toArray(new NamedTextColor[0]))
        .build();

    return miniMessageWrapper.mmParse(Nicks.hooks().applyPlaceHolders(source, Nicks.config().CHAT_FORMAT))
        // Replace display name placeholder with HexNicks nick
        .replaceText(TextReplacementConfig.builder().matchLiteral("{displayname}")
            .replacement(Nicks.core().getDisplayName(source)).build()
        )
        // Replace prefix placeholder with Vault prefix
        .replaceText(TextReplacementConfig.builder().matchLiteral("{prefix}")
            .replacement(Nicks.hooks().vaultPrefix(source)).build()
        )
        // Replace suffix placeholder with Vault Suffix
        .replaceText(TextReplacementConfig.builder().matchLiteral("{suffix}")
            .replacement(Nicks.hooks().vaultSuffix(source)).build()
        )
        // Replace message placeholder with the formatted message from the event
        .replaceText(TextReplacementConfig.builder().matchLiteral("{message}")
            .replacement(miniMessageWrapper.mmParse(message)).build()
        );
  }
}
