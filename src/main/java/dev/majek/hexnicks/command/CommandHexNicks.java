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
import dev.majek.hexnicks.config.NicksMessages;
import dev.majek.hexnicks.util.TabCompleterBase;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handles <code>/hexnicks</code> commands for reloading and editing the config.
 */
public class CommandHexNicks implements TabExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, @NotNull String[] args) {
    if (args.length == 0) {
      return false;
    }

    switch (args[0].toLowerCase(Locale.ROOT)) {
      case "reload": {
        if (!sender.hasPermission("hexnicks.reload")) {
          NicksMessages.NO_PERMISSION.send(sender);
          return true;
        }
        Nicks.core().reload();
        NicksMessages.PLUGIN_RELOADED.send(sender);
        return true;
      }
      case "config-editor": {
        if (!sender.hasPermission("hexnicks.config-editor")) {
          NicksMessages.NO_PERMISSION.send(sender);
          return true;
        }
        if (args.length < 2) {
          return false;
        }
        if (args[1].equalsIgnoreCase("new")) {
          NicksMessages.NEW_EDITOR.send(sender, Nicks.config().toWeb());
        } else if (args[1].equalsIgnoreCase("apply") && args.length == 3) {
          try {
            Nicks.config().fromWeb(args[2]);
          } catch (IllegalArgumentException ex) {
            NicksMessages.INVALID_LINK.send(sender);
            Nicks.logging().error("Invalid link provided in '/hexnicks config-editor apply'. Expected a link from " +
                "either paste.majek.dev or bytebin.majek.dev with a 7 digit page id. Ex. 'https://paste.majek.dev/abcde45'");
            return true;
          }
          NicksMessages.EDITOR_APPLIED.send(sender, args[2]);
        } else {
          return false;
        }
      }
      case "latest-log": {
        if (!sender.hasPermission("hexnicks.view-log")) {
          NicksMessages.NO_PERMISSION.send(sender);
          return true;
        }

        sender.sendMessage("Link: " + Nicks.logging().latestToPasteBin());
        return true;
      }
      default:
        return false;
    }
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                              @NotNull String alias, @NotNull String[] args) {
    if (args.length == 1) {
      return TabCompleterBase.filterStartingWith(args[0], Arrays.asList("reload", "config-editor", "latest-log"));
    } else if (args[0].equalsIgnoreCase("config-editor") && args.length == 2) {
      return TabCompleterBase.filterStartingWith(args[1], Arrays.asList("new", "apply"));
    } else {
      return Collections.emptyList();
    }
  }
}
