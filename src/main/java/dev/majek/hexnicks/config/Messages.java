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

import dev.majek.hexnicks.HexNicks;
import dev.majek.hexnicks.util.MiscUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles all plugin messages.
 */
public interface Messages {

  Args0 INVALID_SENDER = () -> MiscUtils.configString("messages.invalidSender",
      "<red>You must be in-game to use this command.");

  Args1<String> UNKNOWN_PLAYER = playerName -> MiscUtils.configString("messages.unknownPlayer",
      "<red>Unknown player %player%.").replaceText(TextReplacementConfig.builder()
      .matchLiteral("%player%").replacement(playerName).build());

  Args0 NO_PERMISSION = () -> MiscUtils.configString("messages.noPermission",
      "<red>You do not have permission to execute this command.");

  Args1<Integer> TOO_SHORT = minLength -> MiscUtils.configString("messages.tooShort",
      "<red>That nickname is too short. Minimum length is %length% characters.")
      .replaceText(TextReplacementConfig.builder().matchLiteral("%length%")
          .replacement(String.valueOf(minLength)).build());

  Args1<Integer> TOO_LONG = maxLength -> MiscUtils.configString("messages.tooLong",
      "<red>That nickname is too long. Maximum length is %length% characters.")
      .replaceText(TextReplacementConfig.builder().matchLiteral("%length%")
      .replacement(String.valueOf(maxLength)).build());

  Args0 NON_ALPHANUMERIC = () -> MiscUtils.configString("messages.nonAlphanumeric",
      "<red>Your nickname must be alphanumeric.");

  Args1<Component> NICKNAME_SET = nickname -> MiscUtils.configString("messages.nicknameSet",
      "<gray>Your nickname has been set to: <white>%nick%<gray>.")
      .replaceText(TextReplacementConfig.builder().matchLiteral("%nick%").replacement(nickname).build());

  Args2<Player, Component> NICKNAME_SET_OTHER = (player, nickname) -> MiscUtils
      .configStringPlaceholders("messages.nicknameSetOther", "<aqua>%player%<gray>'s nickname has been " +
          "set to: <white>%nick%<gray>.", player)
      .replaceText(TextReplacementConfig.builder().matchLiteral("%player%").replacement(player.getName()).build())
      .replaceText(TextReplacementConfig.builder().matchLiteral("%nick%").replacement(nickname).build());

  Args0 NICKNAME_REMOVED = () -> MiscUtils.configString("messages.nicknameRemoved", "<gray>Nickname removed.");

  Args1<Player> NICKNAME_REMOVED_OTHER = target -> MiscUtils.configStringPlaceholders(
      "messages.nicknameRemovedOther", "<aqua>%player%<gray>'s nickname removed.", target)
      .replaceText(TextReplacementConfig.builder().matchLiteral("%player%").replacement(target.getName()).build());

  Args0 ONLY_COLOR_CODES = () -> MiscUtils.configString("messages.onlyColorCodes",
      "<red>You may only include color codes.");

  Args0 PLUGIN_RELOADED = () -> MiscUtils.configString("messages.pluginReloaded", "<green>Plugin reloaded.");

  Args2<String, Component> REALNAME = (name, nickname) -> MiscUtils
      .configString("messages.realname", "%nickname% <gray>is <aqua>%name%<gray>.")
      .replaceText(TextReplacementConfig.builder().matchLiteral("%nickname%").replacement(nickname).build())
      .replaceText(TextReplacementConfig.builder().matchLiteral("%name%").replacement(name).build());

  Args2<String, Component> FAKENAME = (name, nickname) -> MiscUtils
      .configString("messages.fakename", "%name% <gray>is <aqua>%nickname%<gray>.")
      .replaceText(TextReplacementConfig.builder().matchLiteral("%nickname%").replacement(nickname).build())
      .replaceText(TextReplacementConfig.builder().matchLiteral("%name%").replacement(name).build());

  Args0 UPDATE = () -> MiscUtils.configString("messages.update",
      "<gray>HexNicks has an update! View <click:open_url:https://www.spigotmc.org/resources/83554/><aqua>here</aqua></click>.");

  Args0 NICKNAME_TAKEN = () -> MiscUtils.configString("messages.nicknameTaken",
      "<red>That nickname is taken by another player! Please choose a different one.");

  Args1<String> NEW_EDITOR = (link) -> MiniMessage.miniMessage().deserialize(HexNicks.core().getConfig().getString(
      "messages.newEditor",
      "<green>Edit config <click:open_url:'%link%'><aqua><u>here<u/></aqua></click>. When you're done, save your " +
          "changes and use <click:suggest_command:/hexnicks config-editor apply><aqua>/hexnicks config-editor " +
          "apply <link></aqua></click> to apply them."
  ).replace("%link%", link));

  Args1<String> EDITOR_APPLIED = (link) -> MiniMessage.miniMessage().deserialize(HexNicks.core().getConfig().getString(
      "messages.editorApplied",
      "<green>Changes from <click:open_url:'%link%'><aqua><u>%link%<u/></aqua></click> applied."
  ).replace("%link%", link));

  Args0 INVALID_LINK = () -> MiscUtils.configString("messages.invalidLink",
      "<red>The link provided is not valid! See console for further details.");

  Args1<String> LATEST_LOG = (link) -> MiniMessage.miniMessage().deserialize(HexNicks.core().getConfig().getString(
      "messages.latestLog",
      "<green>View the latest log <click:open_url:'%link%'><aqua><u>here</u></aqua></click>."
  ).replace("%link%", link));

  Args0 WORKING = () -> MiscUtils.configString("messages.working", "<gray>Working...");

  Args0 NOT_ALLOWED = () -> MiscUtils.configString("messages.notAllowed", "<red>That nickname is not allowed!");

  Args2<Player, Component> ANNOUNCE_NICK = (player, nickname) -> MiscUtils
          .configStringPlaceholders("messages.joinAnnouncement", "<yellow>%player% has the nickname</yellow> %nick%", player)
          .replaceText(TextReplacementConfig.builder().matchLiteral("%player%").replacement(player.getName()).build())
          .replaceText(TextReplacementConfig.builder().matchLiteral("%nick%").replacement(nickname).build());

  /**
   * A message that has no arguments that need to be replaced.
   */
  interface Args0 {
    Component build();

    default void send(CommandSender sender) {
      MiscUtils.sendMessage(sender, build());
    }
  }

  /**
   * A message that has one argument that needs to be replaced.
   */
  interface Args1<A0> {
    Component build(A0 arg0);

    default void send(CommandSender sender, A0 arg0) {
      MiscUtils.sendMessage(sender, build(arg0));
    }

    default void announce(A0 arg0) {
      MiscUtils.announceMessage(build(arg0));
    }
  }

  /**
   * A message that has two arguments that need to be replaced.
   */
  interface Args2<A0, A1> {
    Component build(A0 arg0, A1 arg1);

    default void send(CommandSender sender, A0 arg0, A1 arg1) {
      MiscUtils.sendMessage(sender, build(arg0, arg1));
    }

    default void announce(A0 arg0, A1 arg1) {
      MiscUtils.announceMessage(build(arg0, arg1));
    }
  }
}
