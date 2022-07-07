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
import dev.majek.hexnicks.api.SetNickOtherEvent;
import dev.majek.hexnicks.config.Messages;
import dev.majek.hexnicks.message.MiniMessageWrapper;
import dev.majek.hexnicks.util.MiscUtils;
import dev.majek.hexnicks.util.TabCompleterBase;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.Component;
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
      Messages.UNKNOWN_PLAYER.send(sender, args[0]);
      return true;
    }

    // Remove first element of array and get the nickname input
    String[] newArgs = new String[args.length - 1];
    System.arraycopy(args, 1, newArgs, 0, args.length - 1);
    String nickInput = String.join(" ", newArgs);

    Component nickname = MiniMessageWrapper.builder()
        .gradients(target.hasPermission("hexnicks.color.gradient"))
        .hexColors(target.hasPermission("hexnicks.color.hex"))
        .standardColors(true)
        .legacyColors(HexNicks.config().LEGACY_COLORS)
        .removeTextDecorations(HexNicks.config().NICK_OTHER_OVERRIDE ? MiscUtils.blockedDecorations(target)
            : Collections.emptyList())
        .removeColors(HexNicks.config().NICK_OTHER_OVERRIDE ? MiscUtils.blockedColors(target)
            : Collections.emptyList())
        .build().mmParse(nickInput);
    String plainTextNick = PlainTextComponentSerializer.plainText().serialize(nickname);
    int maxLength = HexNicks.config().MAX_LENGTH;
    int minLength = HexNicks.config().MIN_LENGTH;

    // Make sure the nickname is alphanumeric if that's enabled
    if (HexNicks.config().REQUIRE_ALPHANUMERIC) {
      if (!plainTextNick.matches("[a-zA-Z0-9]+")) {
        Messages.NON_ALPHANUMERIC.send(sender);
        return true;
      }
    }

    // Make sure the nickname isn't too short
    if (plainTextNick.length() < minLength) {
      Messages.TOO_SHORT.send(sender, minLength);
      return true;
    }

    // Make sure the nickname isn't too long
    if (plainTextNick.length() > maxLength) {
      Messages.TOO_LONG.send(sender, maxLength);
      return true;
    }

    // Call event
    SetNickOtherEvent nickEvent = new SetNickOtherEvent(sender, target,
        nickname, HexNicks.core().getDisplayName(target));
    HexNicks.api().callEvent(nickEvent);
    if (nickEvent.isCancelled()) {
      return true;
    }
    final Component finalNick = nickEvent.newNick();

    // Send loading message
    Messages.WORKING.send(target);

    // Asynchronously check to make sure the nickname isn't taken
    HexNicks.core().getServer().getScheduler().runTaskAsynchronously(HexNicks.core(), () -> {
      // Make sure the nickname isn't taken
      if (!MiscUtils.preventDuplicates(finalNick, target)) {
        // Set nick
        HexNicks.core().setNick(target, finalNick);
        Messages.NICKNAME_SET.send(target, finalNick);
      }
    });

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
