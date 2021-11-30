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
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

/**
 * Handles Paper server software.
 */
public final class PaperServer implements ServerSoftware {

  @Override
  public Component getNick(Player player) {
    return player.displayName();
  }

  @Override
  public void setNick(@NotNull Player player, @NotNull Component nickname) {
    Nicks.core().getNickMap().put(player.getUniqueId(), nickname);
    player.displayName(nickname);
    if (Nicks.config().TAB_NICKS) {
      player.playerListName(nickname);
    }
    Nicks.storage().saveNick(player);
  }

  @Override
  public void removeNick(@NotNull Player player) {
    Nicks.core().getNickMap().remove(player.getUniqueId());
    player.displayName(Component.text(player.getName()));
    if (Nicks.config().TAB_NICKS) {
      player.playerListName(Component.text(player.getName()));
    }
    Nicks.storage().removeNick(player.getUniqueId());
  }

  @Override
  public void sendMessage(CommandSender sender, Component message) {
    sender.sendMessage(message);
  }

  @Override
  public String softwareName() {
    return "PaperMC";
  }

  /**
   * Fires on chat to format. Lowest priority to allow other plugins to modify over us.
   *
   * @param event AsyncChatEvent.
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void onChat(AsyncChatEvent event) {
    if (Nicks.config().CHAT_FORMATTER) {
      Nicks.debug("paper chat event fired");
      event.renderer((source, sourceDisplayName, message, viewer) -> MiniMessage.miniMessage().parse(Nicks.config().CHAT_FORMAT)
          .replaceText(TextReplacementConfig.builder().matchLiteral("{displayname}").replacement(Nicks.core().getDisplayName(source)).build())
          .replaceText(TextReplacementConfig.builder().matchLiteral("{prefix}").replacement(LegacyComponentSerializer.builder().hexColors()
              .useUnusualXRepeatedCharacterHexFormat().build().deserialize(Nicks.hooks().vaultPrefix(source))).build())
          .replaceText(TextReplacementConfig.builder().matchLiteral("{suffix}").replacement(LegacyComponentSerializer.builder().hexColors()
              .useUnusualXRepeatedCharacterHexFormat().build().deserialize(Nicks.hooks().vaultSuffix(source))).build())
          .replaceText(TextReplacementConfig.builder().matchLiteral("{message}").replacement(
              MiniMessageWrapper.builder().legacyColors(Nicks.config().LEGACY_COLORS)
                  .advancedTransformations(source.hasPermission("hexnicks.chat.advanced")).build()
                  .mmParse(PlainTextComponentSerializer.plainText().serialize(message))
          ).build()));
    }
  }
}