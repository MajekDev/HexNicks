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
package dev.majek.hexnicks.config;

import dev.majek.hexnicks.Nicks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static dev.majek.hexnicks.Nicks.utils;

/**
 * Handles all plugin messages.
 */
public interface NicksMessages {

  Args0 INVALID_SENDER = () -> utils().configString("messages.invalidSender",
      "<red>You must be in-game to use this command.");

  Args1<String> UNKNOWN_PLAYER = playerName -> utils().configString("messages.unknownPlayer",
      "<red>Unknown player %player%.").replaceText(TextReplacementConfig.builder()
      .matchLiteral("%player%").replacement(playerName).build());

  Args0 NO_PERMISSION = () -> utils().configString("messages.noPermission",
      "<red>You do not have permission to execute this command.");

  Args1<Integer> TOO_SHORT = minLength -> utils().configString("messages.tooShort",
      "<red>That nickname is too short. Minimum length is %length% characters.")
      .replaceText(TextReplacementConfig.builder().matchLiteral("%length%")
          .replacement(String.valueOf(minLength)).build());

  Args1<Integer> TOO_LONG = maxLength -> utils().configString("messages.tooLong",
      "<red>That nickname is too long. Maximum length is %length% characters.")
      .replaceText(TextReplacementConfig.builder().matchLiteral("%length%")
      .replacement(String.valueOf(maxLength)).build());

  Args0 NON_ALPHANUMERIC = () -> utils().configString("messages.nonAlphanumeric",
      "<red>Your nickname must be alphanumeric.");

  Args1<Component> NICKNAME_SET = nickname -> utils().configString("messages.nicknameSet",
      "<gray>Your nickname has been set to: <white>%nick%<gray>.")
      .replaceText(TextReplacementConfig.builder().matchLiteral("%nick%").replacement(nickname).build());

  Args2<Player, Component> NICKNAME_SET_OTHER = (player, nickname) -> utils()
      .configStringPlaceholders("messages.nicknameSetOther", "<aqua>%player%<gray>'s nickname has been " +
          "set to: <white>%nick%<gray>.", player)
      .replaceText(TextReplacementConfig.builder().matchLiteral("%player%").replacement(player.getName()).build())
      .replaceText(TextReplacementConfig.builder().matchLiteral("%nick%").replacement(nickname).build());

  Args0 NICKNAME_REMOVED = () -> utils().configString("messages.nicknameRemoved", "<gray>Nickname removed.");

  Args1<Player> NICKNAME_REMOVED_OTHER = target -> utils().configStringPlaceholders(
      "messages.nicknameRemovedOther", "<aqua>%player%<gray>'s nickname removed.", target)
      .replaceText(TextReplacementConfig.builder().matchLiteral("%player%").replacement(target.getName()).build());

  Args0 ONLY_COLOR_CODES = () -> utils().configString("messages.onlyColorCodes",
      "<red>You may only include color codes.");

  Args0 PLUGIN_RELOADED = () -> utils().configString("messages.pluginReloaded", "<green>Plugin reloaded.");

  Args2<String, Component> REALNAME = (name, nickname) -> Nicks.utils()
      .configString("messages.realname", "%nickname% <gray>is <aqua>%name%<gray>.")
      .replaceText(TextReplacementConfig.builder().matchLiteral("%nickname%").replacement(nickname).build())
      .replaceText(TextReplacementConfig.builder().matchLiteral("%name%").replacement(name).build());

  Args0 UPDATE = () -> Nicks.utils().configString("messages.update",
      "<gray>HexNicks has an update! View <click:open_url:https://www.spigotmc.org/resources/83554/><aqua>here</aqua></click>.");

  Args0 NICKNAME_TAKEN = () -> Nicks.utils().configString("messages.nicknameTaken",
      "<red>That nickname is taken by another player! Please choose a different one.");

  /**
   * A message that has no arguments that need to be replaced.
   */
  interface Args0 {
    Component build();

    default void send(CommandSender sender) {
      Nicks.software().sendMessage(sender, build());
    }
  }

  /**
   * A message that has one argument that needs to be replaced.
   */
  interface Args1<A0> {
    Component build(A0 arg0);

    default void send(CommandSender sender, A0 arg0) {
      Nicks.software().sendMessage(sender, build(arg0));
    }
  }

  /**
   * A message that has two arguments that need to be replaced.
   */
  interface Args2<A0, A1> {
    Component build(A0 arg0, A1 arg1);

    default void send(CommandSender sender, A0 arg0, A1 arg1) {
      Nicks.software().sendMessage(sender, build(arg0, arg1));
    }
  }
}
