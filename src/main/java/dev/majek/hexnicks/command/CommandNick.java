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
import dev.majek.hexnicks.api.SetNickEvent;
import dev.majek.hexnicks.config.NicksMessages;
import java.util.Collections;
import java.util.List;

import dev.majek.hexnicks.util.MiniMessageWrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handles <code>/nick</code> command execution and tab completion.
 */
public class CommandNick implements TabExecutor {

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

    MiniMessageWrapper wrapper = new MiniMessageWrapper(nickInput);

    // Check permissions for colors
    if(!player.hasPermission("hexnicks.nick.gradient")) {
      wrapper.removeGradients();
    }
    if(!player.hasPermission("hexnicks.nick.hex")) {
      wrapper.removeHex();
    }
    if(!player.hasPermission("hexnicks.nick.color")) {
      wrapper.removeAllTokens();
    }

    Component nickname = wrapper.mmParse();
    String plainTextNick = PlainTextComponentSerializer.plainText().serialize(nickname);
    int maxLength = Nicks.config().MAX_LENGTH;
    int minLength = Nicks.config().MIN_LENGTH;

    // Make sure the nickname is alphanumeric if that's enabled
    if (Nicks.config().REQUIRE_ALPHANUMERIC) {
      if (!plainTextNick.matches("[a-zA-Z0-9]+")) {
        NicksMessages.NON_ALPHANUMERIC.send(player);
        return true;
      }
    }

    // Set the nickname to the default color if there's no color specified
    nickname = nickname.colorIfAbsent(Nicks.config().DEFAULT_NICK_COLOR);

    // Make sure the nickname isn't too short
    if (plainTextNick.length() < minLength) {
      NicksMessages.TOO_SHORT.send(player, minLength);
      return true;
    }

    // Make sure the nickname isn't too long
    if (plainTextNick.length() > maxLength) {
      NicksMessages.TOO_LONG.send(player, maxLength);
      return true;
    }

    // Call event
    SetNickEvent nickEvent = new SetNickEvent(player, nickname,
        Nicks.core().getDisplayName(player));
    Nicks.api().callEvent(nickEvent);
    if (nickEvent.isCancelled()) {
      return true;
    }

    // Set nick
    Nicks.core().setNick(player, nickEvent.newNick());
    NicksMessages.NICKNAME_SET.send(player, nickEvent.newNick());

    return true;
  }

  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                              @NotNull String label, @NotNull String[] args) {
    return Collections.emptyList();
  }
}