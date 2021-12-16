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

package dev.majek.hexnicks.command;

import dev.majek.hexnicks.Nicks;
import dev.majek.hexnicks.api.NickColorEvent;
import dev.majek.hexnicks.config.NicksMessages;
import dev.majek.hexnicks.util.MiniMessageWrapper;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
    if (!(sender instanceof Player)) {
      NicksMessages.INVALID_SENDER.send(sender);
      return true;
    }
    Player player = (Player) sender;

    if (args.length == 0) {
      return false;
    }

    String nickInput = String.join(" ", args);

    // Check if we're supporting legacy
    if (Nicks.config().LEGACY_COLORS) {
      nickInput = Nicks.utils().legacyToMini(nickInput);
    }

    // If there are no colors the length should be 0
    String plainTextInput = PlainTextComponentSerializer.plainText()
        .serialize(MiniMessage.miniMessage().parse(nickInput));
    if (plainTextInput.length() > 0) {
      NicksMessages.ONLY_COLOR_CODES.send(player);
      return true;
    }

    MiniMessageWrapper wrapper = MiniMessageWrapper.builder()
        .gradients(player.hasPermission("hexnicks.nick.gradient"))
        .hexColors(player.hasPermission("hexnicks.nick.hex"))
        .standardColors(player.hasPermission("hexnicks.nick.color"))
        .legacyColors(Nicks.config().LEGACY_COLORS)
        .removeTextDecorations(Nicks.config().DISABLED_DECORATIONS.toArray(new TextDecoration[0]))
        .build();

    // Get the players current nickname to apply color codes to
    String plainTextNick = PlainTextComponentSerializer.plainText()
        .serialize(Nicks.core().getDisplayName(player));

    // Remove nickname prefix if essentials is hooked
    if (Nicks.hooks().isEssentialsHooked()) {
      String nickPrefix = Nicks.hooks().getEssNickPrefix();
      if (nickPrefix != null && plainTextNick.startsWith(nickPrefix)) {
        plainTextNick = plainTextNick.substring(nickPrefix.length());
      }
    }

    Component nickname = wrapper.mmParse(wrapper.mmString(nickInput) + plainTextNick);

    // Make sure the nickname isn't taken
    if (Nicks.utils().preventDuplicates(nickname, player)) {
      return true;
    }

    // Call event
    NickColorEvent colorEvent = new NickColorEvent(player, nickname,
        Nicks.core().getDisplayName(player));
    Nicks.api().callEvent(colorEvent);
    if (colorEvent.isCancelled()) {
      return true;
    }

    // Set nick
    Nicks.core().setNick(player, colorEvent.newNick());
    NicksMessages.NICKNAME_SET.send(player, colorEvent.newNick());

    return true;
  }

  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                              @NotNull String label, @NotNull String[] args) {
    return Collections.emptyList();
  }
}