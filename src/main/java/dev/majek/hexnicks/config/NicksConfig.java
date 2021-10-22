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

package dev.majek.hexnicks.config;

import dev.majek.hexnicks.Nicks;
import net.kyori.adventure.text.format.TextColor;

/**
 * Handles all config options in the plugin.
 */
public class NicksConfig {

  public Boolean    TAB_NICKS;
  public Integer    MAX_LENGTH;
  public Integer    MIN_LENGTH;
  public Boolean    REQUIRE_ALPHANUMERIC;
  public Boolean    CHAT_FORMATTER;
  public String     CHAT_FORMAT;
  public Boolean    LEGACY_COLORS;
  public TextColor  DEFAULT_NICK_COLOR;
  public TextColor  DEFAULT_USERNAME_COLOR;
  public Boolean    UPDATE_PROMPT;
  public Boolean    OVERRIDE_ESSENTIALS;
  public Boolean    DEBUG;

  public NicksConfig() {
    reload();
  }

  /**
   * Reload the config values.
   */
  public void reload() {
    TAB_NICKS = Nicks.core().getConfig().getBoolean("tab-nicks", false);
    MAX_LENGTH = Nicks.core().getConfig().getInt("max-length", 20);
    MIN_LENGTH = Nicks.core().getConfig().getInt("min-length", 3);
    REQUIRE_ALPHANUMERIC = Nicks.core().getConfig().getBoolean("require-alphanumeric", false);
    CHAT_FORMATTER = Nicks.core().getConfig().getBoolean("chat-formatter", false);
    CHAT_FORMAT = Nicks.core().getConfig().getString("chat-format", "{displayname}: {message}");
    LEGACY_COLORS = Nicks.core().getConfig().getBoolean("legacy-colors", false);
    DEFAULT_NICK_COLOR = TextColor.fromHexString(Nicks.core().getConfig().getString("default-nick-color", "#FFFFFF"));
    DEFAULT_USERNAME_COLOR = TextColor.fromHexString(Nicks.core().getConfig().getString("default-username-color", "#FFFFFF"));
    UPDATE_PROMPT = Nicks.core().getConfig().getBoolean("update-prompt", true);
    OVERRIDE_ESSENTIALS = Nicks.core().getConfig().getBoolean("override-essentials", true);
    DEBUG = Nicks.core().getConfig().getBoolean("debug", false);
  }
}