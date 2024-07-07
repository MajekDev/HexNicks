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
package dev.majek.hexnicks.event;

import dev.majek.hexnicks.HexNicks;
import dev.majek.hexnicks.api.HexNicksApi;
import dev.majek.hexnicks.config.Messages;
import dev.majek.hexnicks.message.MiniMessageWrapper;
import dev.majek.hexnicks.util.MiscUtils;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.awt.*;
import java.util.List;

/**
 * <p>Handles the player join event.</p>
 * <p>Sets the player displayname if they have a nickname in the map.</p>
 */
public class PlayerJoin implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerJoin(PlayerJoinEvent event) {
    HexNicks.storage().updateNicks();
    Player player = event.getPlayer();
    boolean shouldFormat = HexNicks.config().FORMAT_JOIN;

    // Set the joining player's nickname to their stored nickname if they have one
    HexNicks.storage().hasNick(player.getUniqueId()).whenCompleteAsync((aBoolean, throwable) -> {
      if (aBoolean) {
        HexNicks.logging().debug("Player " + player.getName() + " joined and has nickname, setting...");
        HexNicks.storage().getNick(player.getUniqueId()).whenCompleteAsync((component, throwable1) -> {
          if (HexNicks.config().ANNOUNCE_NICKS_ON_JOIN) {
            Messages.ANNOUNCE_NICK.announce(player, component);
          }
          HexNicks.core().setNick(player, component);
          if(shouldFormat) { JoinMessage(player, component); }
        });
      } else {
        HexNicks.logging().debug("Player " + player.getName() + " joined and has no nickname.");
        if(shouldFormat) { JoinMessage(player, Component.text(player.getName())); }
      }
    });

    // Update prompt
    if (event.getPlayer().isOp() && HexNicks.core().hasUpdate() && HexNicks.config().UPDATE_PROMPT) {
      Messages.UPDATE.send(event.getPlayer());
    }

    Component name = HexNicks.core().getNickMap().get(player.getUniqueId());
    if (name != null) {
      name = Component.text(player.getName());
    }

    if(shouldFormat) { event.joinMessage(Component.text().content("").build()); }
  }

  private void JoinMessage (Player source, Component name) {
    final MiniMessageWrapper miniMessageWrapper = MiniMessageWrapper.builder()
            .advancedTransformations(source.hasPermission("hexnicks.chat.advanced"))
            .gradients(source.hasPermission("hexnicks.color.gradient"))
            .hexColors(source.hasPermission("hexnicks.color.hex"))
            .legacyColors(HexNicks.config().LEGACY_COLORS)
            .removeTextDecorations(MiscUtils.blockedDecorations(source))
            .removeColors(MiscUtils.blockedColors(source))
            .build();

    Component text = miniMessageWrapper.mmParse(HexNicks.hooks().applyPlaceHolders(source, HexNicks.config().JOIN_FORMAT))
            // Replace display name placeholder with HexNicks nick
            .replaceText(TextReplacementConfig.builder().matchLiteral("{displayname}")
                    .replacement(name).build()
            )
            // Replace prefix placeholder with Vault prefix
            .replaceText(TextReplacementConfig.builder().matchLiteral("{prefix}")
                    .replacement(HexNicks.hooks().vaultPrefix(source)).build()
            )
            // Replace suffix placeholder with Vault Suffix
            .replaceText(TextReplacementConfig.builder().matchLiteral("{suffix}")
                    .replacement(HexNicks.hooks().vaultSuffix(source)).build()
            );

    Bukkit.getServer().getOnlinePlayers().forEach(player -> player.sendMessage(text));
  }
}
