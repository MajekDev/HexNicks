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
import dev.majek.hexnicks.config.Messages;
import dev.majek.hexnicks.util.TabCompleterBase;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handles <code>/fakename</code> command execution and tab completion.
 */
public class CommandFakeName implements TabExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, @NotNull String[] args) {
    if (args.length == 0) {
      return false;
    }

    OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
    if (player == null) {
      Messages.UNKNOWN_PLAYER.send(sender, String.join(" ", args));
      return true;
    }

    CompletableFuture<Component> nickname = HexNicks.api().getStoredNick(player);
    if (nickname == null) {
      Messages.UNKNOWN_PLAYER.send(sender, String.join(" ", args));
      return true;
    }

    nickname.whenComplete(((component, throwable) -> Messages.FAKENAME.send(sender, player.getName(), component)));
    return true;
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                              @NotNull String alias, @NotNull String[] args) {
    Stream<String> names = Bukkit.getOnlinePlayers().stream().map(OfflinePlayer::getName);
    return TabCompleterBase.filterStartingWith(args[0], names);
  }
}
