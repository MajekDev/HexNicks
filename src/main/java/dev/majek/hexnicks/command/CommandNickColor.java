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
package dev.majek.hexnicks.command;

import dev.majek.hexnicks.HexNicks;
import dev.majek.hexnicks.api.NickColorEvent;
import dev.majek.hexnicks.config.Messages;
import dev.majek.hexnicks.message.MiniMessageWrapper;
import java.util.Collections;
import java.util.List;
import dev.majek.hexnicks.util.MiscUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handles <code>/nickcolor</code> command execution and tab completion.
 */
public class CommandNickColor implements TabExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, @NotNull String[] args) {
    // Console cannot have a nickname
    if (!(sender instanceof Player player)) {
      Messages.INVALID_SENDER.send(sender);
      return true;
    }

    if (args.length == 0) {
      return false;
    }

    String nickInput = String.join(" ", args);

    // If there are no colors the length should be 0
    String plainTextInput = PlainTextComponentSerializer.plainText()
        .serialize(MiniMessageWrapper.legacy().mmParse(nickInput));
    if (plainTextInput.length() > 0 && !plainTextInput.equals(player.getName())) {
      Messages.ONLY_COLOR_CODES.send(player);
      return true;
    }

    MiniMessageWrapper wrapper = MiniMessageWrapper.builder()
        .gradients(player.hasPermission("hexnicks.color.gradient"))
        .hexColors(player.hasPermission("hexnicks.color.hex"))
        .standardColors(true)
        .legacyColors(HexNicks.config().LEGACY_COLORS)
        .removeTextDecorations(MiscUtils.blockedDecorations(player))
        .removeColors(MiscUtils.blockedColors(player))
        .build();

    // Get the players current nickname to apply color codes to
    String plainTextNick = PlainTextComponentSerializer.plainText()
        .serialize(HexNicks.core().getDisplayName(player));

    // Remove nickname prefix if essentials is hooked
    if (HexNicks.hooks().isEssentialsHooked()) {
      String nickPrefix = HexNicks.hooks().getEssNickPrefix();
      if (nickPrefix != null && plainTextNick.startsWith(nickPrefix)) {
        plainTextNick = plainTextNick.substring(nickPrefix.length());
      }
    }

    Component nickname = wrapper.mmParse(
        wrapper.mmString(nickInput) + (plainTextInput.length() == 0 ? plainTextNick : "")
    );

    // Call event
    NickColorEvent colorEvent = new NickColorEvent(player, nickname,
        HexNicks.core().getDisplayName(player));
    HexNicks.api().callEvent(colorEvent);
    if (colorEvent.isCancelled()) {
      return true;
    }
    final Component finalNick = colorEvent.newNick();

    // Send loading message
    Messages.WORKING.send(player);

    // Asynchronously check to make sure the nickname isn't taken
    HexNicks.scheduler().runTaskAsynchronously(() -> {
      // Make sure the nickname isn't taken
      if (!MiscUtils.preventDuplicates(finalNick, player)) {
        // Set nick
        HexNicks.core().setNick(player, finalNick);
        Messages.NICKNAME_SET.send(player, finalNick);
      }
    });

    return true;
  }

  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                              @NotNull String label, @NotNull String[] args) {
    return Collections.emptyList();
  }
}
