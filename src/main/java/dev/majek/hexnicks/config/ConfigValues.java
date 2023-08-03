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

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.majek.hexnicks.HexNicks;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;
import dev.majek.hexnicks.util.MiscUtils;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

/**
 * Handles all config options in the plugin.
 */
public class ConfigValues {

  public Boolean              TAB_NICKS;
  public Boolean              ANNOUNCE_NICKS_ON_JOIN;
  public Integer              MAX_LENGTH;
  public Integer              MIN_LENGTH;
  public Boolean              REQUIRE_ALPHANUMERIC;
  public Boolean              CHAT_FORMATTER;
  public String               CHAT_FORMAT;
  public Boolean              LEGACY_COLORS;
  public TextColor            DEFAULT_NICK_COLOR;
  public TextColor            DEFAULT_USERNAME_COLOR;
  public Boolean              UPDATE_PROMPT;
  public Boolean              OVERRIDE_ESSENTIALS;
  public Boolean              NICK_OTHER_OVERRIDE;
  public Boolean              PREVENT_DUPLICATE_NICKS;
  public Boolean              PREVENT_DUPLICATE_NICKS_STRICT;
  public List<String>         BLOCKED_NICKNAMES;
  public Boolean              DEBUG;

  public ConfigValues() {
    this.reload();
  }

  /**
   * Reload the config values.
   */
  public void reload() {
    TAB_NICKS = HexNicks.core().getConfig().getBoolean("tab-nicks", false);
    ANNOUNCE_NICKS_ON_JOIN = HexNicks.core().getConfig().getBoolean("announce-nicks-on-join", false);
    MAX_LENGTH = HexNicks.core().getConfig().getInt("max-length", 20);
    MIN_LENGTH = HexNicks.core().getConfig().getInt("min-length", 3);
    REQUIRE_ALPHANUMERIC = HexNicks.core().getConfig().getBoolean("require-alphanumeric", false);
    CHAT_FORMATTER = HexNicks.core().getConfig().getBoolean("chat-formatter", false);
    CHAT_FORMAT = HexNicks.core().getConfig().getString("chat-format", "{displayname}: {message}");
    LEGACY_COLORS = HexNicks.core().getConfig().getBoolean("legacy-colors", false);
    DEFAULT_NICK_COLOR = TextColor.fromHexString(HexNicks.core().getConfig().getString("default-nick-color", "#FFFFFF"));
    DEFAULT_USERNAME_COLOR = TextColor.fromHexString(HexNicks.core().getConfig().getString("default-username-color", "#FFFFFF"));
    UPDATE_PROMPT = HexNicks.core().getConfig().getBoolean("update-prompt", true);
    OVERRIDE_ESSENTIALS = HexNicks.core().getConfig().getBoolean("override-essentials", true);
    NICK_OTHER_OVERRIDE = HexNicks.core().getConfig().getBoolean("nickother-override", false);
    PREVENT_DUPLICATE_NICKS = HexNicks.core().getConfig().getBoolean("prevent-duplicate-nicks", true);
    PREVENT_DUPLICATE_NICKS_STRICT = HexNicks.core().getConfig().getBoolean("prevent-duplicate-nicks-strict", false);
    BLOCKED_NICKNAMES = HexNicks.core().getConfig().getStringList("blocked-nicknames");
    DEBUG = HexNicks.core().getConfig().getBoolean("debug", false);
  }

  /**
   * Upload the config to bytebin to be edited via pastebin frontend.
   *
   * @return the url to the pastebin for editing
   */
  public @NotNull String toWeb() {
    final HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://bytebin.majek.dev/post"))
        .header("User-Agent", "hexnicks")
        .header("Content-Type", "text/yaml; charset=utf-8")
        .POST(HttpRequest.BodyPublishers.ofString(
            HexNicks.core().getConfig().saveToString()
        )).build();

    final JsonObject json = JsonParser.parseString(MiscUtils.sendRequestAndGetResponse(request)).getAsJsonObject();
    final String url = "https://paste.majek.dev/" + json.get("key").getAsString();
    HexNicks.logging().debug("Uploaded config to " + url + " for updating");

    return url;
  }

  /**
   * Retrieve an updated config from bytebin and save it to the local file.
   *
   * @param link the link to the pastebin or bytebin.
   * @throws IllegalArgumentException if the link is not a valid bytebin link.
   */
  public void fromWeb(@NotNull String link) {
    link = link.replace("paste", "bytebin");
    if (!link.matches("https://bytebin\\.majek\\.dev/([a-zA-Z0-9]{7})")) {
      throw new IllegalArgumentException("The link provided is not a valid bytebin link!");
    }

    HexNicks.logging().debug("Retrieving updated config from " + link);
    final HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(link))
        .header("User-Agent", "hexnicks")
        .GET()
        .build();

    try {
      Files.asCharSink(new File(HexNicks.core().getDataFolder(), "config.yml"), Charsets.UTF_8)
          .write(MiscUtils.sendRequestAndGetResponse(request));
    } catch (final IOException ex) {
      HexNicks.logging().error("Error saving config from editor", ex);
    }

    HexNicks.core().reload();
  }
}
