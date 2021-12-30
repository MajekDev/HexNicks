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
import dev.majek.hexnicks.api.SetNickOtherEvent;
import dev.majek.hexnicks.config.NicksMessages;
import dev.majek.hexnicks.util.MiniMessageWrapper;
import dev.majek.hexnicks.util.TabCompleterBase;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handles <code>/nickother</code> command execution and tab completion.
 */
public class CommandNickOther implements TabExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, @NotNull String[] args) {
    if (args.length < 2) {
      return false;
    }

    // Make sure the target player is online
    Player target = Bukkit.getPlayer(args[0]);
    if (target == null) {
      NicksMessages.UNKNOWN_PLAYER.send(sender, args[0]);
      return true;
    }

    // Remove first element of array and get the nickname input
    String[] newArgs = new String[args.length - 1];
    System.arraycopy(args, 1, newArgs, 0, args.length - 1);
    String nickInput = String.join(" ", newArgs);

    // Check if we're supporting legacy
    if (Nicks.config().LEGACY_COLORS) {
      nickInput = Nicks.utils().legacyToMini(nickInput);
    }

    Component nickname = MiniMessageWrapper.builder()
        .gradients(target.hasPermission("hexnicks.color.gradient"))
        .hexColors(target.hasPermission("hexnicks.color.hex"))
        .standardColors(true)
        .legacyColors(Nicks.config().LEGACY_COLORS)
        .removeTextDecorations(Nicks.config().DISABLED_DECORATIONS.toArray(new TextDecoration[0]))
        .build().mmParse(nickInput);
    String plainTextNick = PlainTextComponentSerializer.plainText().serialize(nickname);
    int maxLength = Nicks.config().MAX_LENGTH;
    int minLength = Nicks.config().MIN_LENGTH;

    // Make sure the nickname is alphanumeric if that's enabled
    if (Nicks.config().REQUIRE_ALPHANUMERIC) {
      if (!plainTextNick.matches("[a-zA-Z0-9]+")) {
        NicksMessages.NON_ALPHANUMERIC.send(sender);
        return true;
      }
    }

    // Make sure the nickname isn't too short
    if (plainTextNick.length() < minLength) {
      NicksMessages.TOO_SHORT.send(sender, minLength);
      return true;
    }

    // Make sure the nickname isn't too long
    if (plainTextNick.length() > maxLength) {
      NicksMessages.TOO_LONG.send(sender, maxLength);
      return true;
    }

    // Make sure the nickname isn't taken
    if (Nicks.utils().preventDuplicates(nickname, sender)) {
      return true;
    }

    // Call event
    SetNickOtherEvent nickEvent = new SetNickOtherEvent(sender, target,
        nickname, Nicks.core().getDisplayName(target));
    Nicks.api().callEvent(nickEvent);
    if (nickEvent.isCancelled()) {
      return true;
    }

    // Set nick
    Nicks.core().setNick(target, nickEvent.newNick());
    NicksMessages.NICKNAME_SET_OTHER.send(sender, target, nickEvent.newNick());

    return true;
  }

  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                              @NotNull String label, @NotNull String[] args) {
    if (args.length == 1) {
      return TabCompleterBase.getOnlinePlayers(args[0]);
    } else {
      return Collections.emptyList();
    }
  }
}
