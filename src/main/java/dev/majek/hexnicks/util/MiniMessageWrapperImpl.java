/*
 * This file is part of MiniMessageWrapper, licensed under the MIT License.
 *
 * Copyright (c) 2021 Majekdor
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

package dev.majek.hexnicks.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.transformation.TransformationRegistry;
import net.kyori.adventure.text.minimessage.transformation.TransformationType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Not public api.
 *
 * @since 2.1.2
 */
@ApiStatus.Internal
final class MiniMessageWrapperImpl implements MiniMessageWrapper {

  @ApiStatus.Internal
  static final MiniMessageWrapper STANDARD = new MiniMessageWrapperImpl(true, true,
      true, false, false);

  @ApiStatus.Internal
  static final MiniMessageWrapper LEGACY = new MiniMessageWrapperImpl(true, true,
      true, true, false);

  @SuppressWarnings("all")
  private final TransformationRegistry allTransformations = TransformationRegistry.builder().add(
      TransformationType.CLICK_EVENT, TransformationType.COLOR, TransformationType.DECORATION,
      TransformationType.FONT, TransformationType.GRADIENT, TransformationType.HOVER_EVENT,
      TransformationType.INSERTION, TransformationType.KEYBIND, TransformationType.RAINBOW,
      TransformationType.TRANSLATABLE
  ).build();

  @SuppressWarnings("all")
  private final TransformationRegistry colorTransformations = TransformationRegistry.builder().add(
      TransformationType.COLOR, TransformationType.DECORATION,
      TransformationType.GRADIENT, TransformationType.RAINBOW
  ).build();

  private final boolean gradients, hexColors, standardColors, legacyColors, advancedTransformations;

  MiniMessageWrapperImpl(boolean gradients, boolean hexColors, boolean standardColors,
                         boolean legacyColors, boolean advancedTransformations) {
    this.gradients = gradients;
    this.hexColors = hexColors;
    this.standardColors = standardColors;
    this.legacyColors = legacyColors;
    this.advancedTransformations = advancedTransformations;
  }

  @Override
  public @NotNull Component mmParse(@NotNull String mmString) {
    return MiniMessage.builder().transformations(
        advancedTransformations ? allTransformations : colorTransformations
    ).build().parse(mmString(mmString));
  }

  @Override
  public @NotNull String mmString(@NotNull String mmString) {
    if (legacyColors) {
      mmString = mmString
          .replace("&0", "<black>")
          .replace("&1", "<dark_blue>")
          .replace("&2", "<dark_green>")
          .replace("&3", "<dark_aqua>")
          .replace("&4", "<dark_red>")
          .replace("&5", "<dark_purple>")
          .replace("&6", "<gold>")
          .replace("&7", "<gray>")
          .replace("&8", "<dark_gray>")
          .replace("&9", "<blue>")
          .replace("&a", "<green>")
          .replace("&b", "<aqua>")
          .replace("&c", "<red>")
          .replace("&d", "<light_purple>")
          .replace("&e", "<yellow>")
          .replace("&f", "<white>")
          .replace("&m", "<underlined>")
          .replace("&m", "<strikethrough>")
          .replace("&k", "<obfuscated>")
          .replace("&o", "<italic>")
          .replace("&l", "<bold>")
          .replace("&r", "<reset>");

      if (hexColors) {
        // parse the nicer pattern: '&#rrggbb' to spigot's: '&x&r&r&g&g&b&b'
        Pattern sixCharHex = Pattern.compile("&#([0-9a-fA-F]{6})");
        Matcher matcher = sixCharHex.matcher(mmString);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
          StringBuilder replacement = new StringBuilder(14).append("&x");
          for (char character : matcher.group(1).toCharArray()) {
            replacement.append('&').append(character);
          }
          matcher.appendReplacement(sb, replacement.toString());
        }
        matcher.appendTail(sb);
        mmString = sb.toString();

        // convert three char nicer hex '&#rgb' to spigot's: '&x&r&r&g&g&b&b'
        Pattern threeCharHex = Pattern.compile("&#([0-9a-fA-F]{3})");
        matcher = threeCharHex.matcher(mmString);
        sb = new StringBuilder();
        while (matcher.find()) {
          StringBuilder replacement = new StringBuilder(14).append("&x");
          for (char character : matcher.group(1).toCharArray()) {
            replacement.append('&').append(character).append("&").append(character);
          }
          matcher.appendReplacement(sb, replacement.toString());
        }
        matcher.appendTail(sb);
        mmString = sb.toString();

        // parse spigot's hex pattern '&x&r&r&g&g&b&b' to mini message's '<#rrggbb>'
        Pattern spigotHexPattern = Pattern.compile("&x(&[0-9a-fA-F]){6}");
        matcher = spigotHexPattern.matcher(mmString);
        sb = new StringBuilder();
        while (matcher.find()) {
          StringBuilder replacement = new StringBuilder(9).append("<#");
          for (char character : matcher.group().toCharArray()) {
            if (character != '&' && character != 'x') {
              replacement.append(character);
            }
          }
          replacement.append(">");
          matcher.appendReplacement(sb, replacement.toString());
        }
        matcher.appendTail(sb);
        mmString = sb.toString();
      } else {
        mmString = mmString.replaceAll("&#([0-9a-fA-F]{6})", "");
        mmString = mmString.replaceAll("&x(&[0-9a-fA-F]){6}", "");
      }
    } else {
      mmString = mmString.replaceAll("(&[0-9a-fA-Fk-oK-OxXrR])+", "");
    }

    if (!gradients) {
      mmString = mmString.replaceAll("<gradient([:#0-9a-fA-F]{8})+>", "");
      mmString = mmString.replaceAll("</gradient>", "");
    }

    if (!hexColors) {
      mmString = mmString.replaceAll("<#([0-9a-fA-F]{6})>", "");
      mmString = mmString.replaceAll("</#([0-9a-fA-F]{6})>", "");
      mmString = mmString.replaceAll("<c:#([0-9a-fA-F]{6})>", "");
      mmString = mmString.replaceAll("</c:#([0-9a-fA-F]{6})>", "");
      mmString = mmString.replaceAll("</c>", "");
      mmString = mmString.replaceAll("<color:#([0-9a-fA-F]{6})>", "");
      mmString = mmString.replaceAll("</color:#([0-9a-fA-F]{6})>", "");
      mmString = mmString.replaceAll("</color>", "");
      mmString = mmString.replaceAll("<colour:#([0-9a-fA-F]{6})>", "");
      mmString = mmString.replaceAll("</colour:#([0-9a-fA-F]{6})>", "");
      mmString = mmString.replaceAll("</colour>", "");
    }

    // can't use regex, it would mess with placeholders
    if (!standardColors) {
      List<String> mmColorTags = new ArrayList<>(Arrays.asList("<black>", "<dark_blue>", "<dark_green>",
          "<dark_aqua>", "<dark_red>", "<dark_purple>", "<gold>", "<gray>", "<dark_gray>", "<blue>", "<green>",
          "<aqua>", "<red>", "<light_purple>", "<yellow>", "<white>", "<underlined>", "<strikethrough>", "<st>",
          "<obfuscated>", "<obf>", "<italic>", "<em>", "<i>", "<bold>", "<b>", "<reset>", "<r>", "<pre>",
          "</black>", "</dark_blue>", "</dark_green>", "</dark_aqua>", "</dark_red>", "</dark_purple>", "</gold>",
          "</gray>", "</dark_gray>", "</blue>", "</green>", "</aqua>", "</red>", "</light_purple>", "</yellow>",
          "</white>", "</underlined>", "</strikethrough>", "</st>", "</obfuscated>", "</obf>", "</italic>",
          "</em>", "</i>", "</bold>", "</b>", "</reset>", "</r>", "</pre>"));
      for (String tag : mmColorTags) {
        mmString = mmString.replace(tag, "");
      }
    }

    return mmString;
  }

  @Override
  public @NotNull Builder toBuilder() {
    return new BuilderImpl(this);
  }

  @ApiStatus.Internal
  static final class BuilderImpl implements Builder {

    private boolean gradients, hexColors, standardColors, legacyColors, advancedTransformations;

    @ApiStatus.Internal
    BuilderImpl() {
      gradients = true;
      hexColors = true;
      standardColors = true;
      legacyColors = false;
      advancedTransformations = false;
    }

    @ApiStatus.Internal
    BuilderImpl(MiniMessageWrapperImpl wrapper) {
      gradients = wrapper.gradients;
      hexColors = wrapper.hexColors;
      standardColors = wrapper.standardColors;
      legacyColors = wrapper.legacyColors;
      advancedTransformations = wrapper.advancedTransformations;
    }

    @Override
    public @NotNull Builder gradients(boolean parse) {
      gradients = parse;
      return this;
    }

    @Override
    public @NotNull Builder hexColors(boolean parse) {
      hexColors = parse;
      return this;
    }

    @Override
    public @NotNull Builder standardColors(boolean parse) {
      standardColors = parse;
      return this;
    }

    @Override
    public @NotNull Builder legacyColors(boolean parse) {
      legacyColors = parse;
      return this;
    }

    @Override
    public @NotNull Builder advancedTransformations(boolean parse) {
      advancedTransformations = parse;
      return this;
    }

    @Override
    public @NotNull MiniMessageWrapper build() {
      return new MiniMessageWrapperImpl(gradients, hexColors, standardColors, legacyColors, advancedTransformations);
    }
  }
}