/*
 * This file is part of HexNicks, licensed under the MIT License.
 *
 * Copyright (c) Majekdor
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
package dev.majek.hexnicks.message;

import com.google.common.collect.ImmutableMap;
import dev.majek.hexnicks.util.MiscUtils;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
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
  static final MiniMessageWrapper STANDARD = new MiniMessageWrapperImpl(
      true,
      true,
      true,
      false,
      false,
      true,
      TagResolver.empty(),
      new HashSet<>(),
      new HashSet<>()
  );

  @ApiStatus.Internal
  static final MiniMessageWrapper LEGACY = new MiniMessageWrapperImpl(
      true,
      true,
      true,
      true,
      false,
      true,
      TagResolver.empty(),
      new HashSet<>(),
      new HashSet<>()
  );

  private final TagResolver allTags = TagResolver.resolver(
      ExtraTags.cssColors(),
      StandardTags.defaults()
  );

  private final TagResolver colorTags = TagResolver.resolver(
      StandardTags.color(),
      StandardTags.decorations(),
      StandardTags.gradient(),
      StandardTags.rainbow()
  );

  private final TagResolver colorTagsPlusCSS = TagResolver.resolver(
      ExtraTags.cssColors(),
      StandardTags.color(),
      StandardTags.decorations(),
      StandardTags.gradient(),
      StandardTags.rainbow()
  );

  private TagResolver colorTags() {
    if (this.cssColors) {
      return this.colorTagsPlusCSS;
    } else {
      return this.colorTags;
    }
  }

  private final boolean
      gradients,
      hexColors,
      standardColors,
      legacyColors,
      advancedTransformations,
      cssColors;
  private final TagResolver placeholderResolver;
  private final Set<TextDecoration> removedTextDecorations;
  private final Set<NamedTextColor> removedColors;

  MiniMessageWrapperImpl(
      final boolean gradients,
      final boolean hexColors,
      final boolean standardColors,
      final boolean legacyColors,
      final boolean advancedTransformations,
      final boolean cssColors,
      final TagResolver placeholderResolver,
      final Set<TextDecoration> removedTextDecorations,
      final Set<NamedTextColor> removedColors
  ) {
    this.gradients = gradients;
    this.hexColors = hexColors;
    this.standardColors = standardColors;
    this.legacyColors = legacyColors;
    this.advancedTransformations = advancedTransformations;
    this.cssColors = cssColors;
    this.placeholderResolver = placeholderResolver;
    this.removedTextDecorations = removedTextDecorations;
    this.removedColors = removedColors;
  }

  @Override
  public @NotNull Component mmParse(@NotNull String mmString) {
    return MiniMessage.builder()
        .tags(TagResolver.resolver(
            this.advancedTransformations ? this.allTags : this.colorTags(),
            this.placeholderResolver
        ))
        .build()
        .deserialize(this.mmString(mmString));
  }

  @Override
  public @NotNull String mmString(@NotNull String mmString) {
    for (NamedTextColor color : this.removedColors) {
      mmString = mmString.replace("<" + color.toString().toLowerCase(Locale.ROOT) + ">", "");
      mmString = mmString.replace("</" + color.toString().toLowerCase(Locale.ROOT) + ">", "");
      mmString = mmString.replace("&" + MiscUtils.legacyCodeFromNamed(color), "");
    }

    if (this.removedTextDecorations.contains(TextDecoration.BOLD)) {
      mmString = mmString.replaceAll("<(/|)(!|)(bold|b)>", "");
      mmString = mmString.replace("&l", "");
    }
    if (this.removedTextDecorations.contains(TextDecoration.ITALIC)) {
      mmString = mmString.replaceAll("<(/|)(!|)(italic|i|em)>", "");
      mmString = mmString.replace("&o", "");
    }
    if (this.removedTextDecorations.contains(TextDecoration.UNDERLINED)) {
      mmString = mmString.replaceAll("<(/|)(!|)(underlined|u)>", "");
      mmString = mmString.replace("&n", "");
    }
    if (this.removedTextDecorations.contains(TextDecoration.STRIKETHROUGH)) {
      mmString = mmString.replaceAll("<(/|)(!|)(strikethrough|st)>", "");
      mmString = mmString.replace("&m", "");
    }
    if (this.removedTextDecorations.contains(TextDecoration.OBFUSCATED)) {
      mmString = mmString.replaceAll("<(/|)(!|)(obfuscated|obf)>", "");
      mmString = mmString.replace("&k", "");
    }

    if (this.legacyColors) {
      if (this.hexColors) {
        // parse the nicer pattern: '&#rrggbb' to spigot's: '&x&r&r&g&g&b&b'
        final Pattern sixCharHex = Pattern.compile("&#([0-9a-fA-F]{6})");
        Matcher matcher = sixCharHex.matcher(mmString);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
          final StringBuilder replacement = new StringBuilder(14).append("&x");
          for (final char character : matcher.group(1).toCharArray()) {
            replacement.append('&').append(character);
          }
          matcher.appendReplacement(sb, replacement.toString());
        }
        matcher.appendTail(sb);
        mmString = sb.toString();

        // convert three char nicer hex '&#rgb' to spigot's: '&x&r&r&g&g&b&b'
        final Pattern threeCharHex = Pattern.compile("&#([0-9a-fA-F]{3})");
        matcher = threeCharHex.matcher(mmString);
        sb = new StringBuilder();
        while (matcher.find()) {
          final StringBuilder replacement = new StringBuilder(14).append("&x");
          for (final char character : matcher.group(1).toCharArray()) {
            replacement.append('&').append(character).append("&").append(character);
          }
          matcher.appendReplacement(sb, replacement.toString());
        }
        matcher.appendTail(sb);
        mmString = sb.toString();

        // parse spigot's hex pattern '&x&r&r&g&g&b&b' to mini message's '<#rrggbb>'
        final Pattern spigotHexPattern = Pattern.compile("&x(&[0-9a-fA-F]){6}");
        matcher = spigotHexPattern.matcher(mmString);
        sb = new StringBuilder();
        while (matcher.find()) {
          final StringBuilder replacement = new StringBuilder(9).append("<#");
          for (final char character : matcher.group().toCharArray()) {
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
        mmString = mmString.replaceAll("[&§]#([0-9a-fA-F]{6})", "");
        mmString = mmString.replaceAll("[&§]x(&[0-9a-fA-F]){6}", "");
      }

      final Pattern legacyCharPattern = Pattern.compile("(?<!\\\\)([&§]([a-f0-9k-or]))");
      mmString = legacyCharPattern.matcher(mmString).replaceAll((result) -> CHAR_COLORS.get(result.group(2).charAt(0)));

      final Pattern escapedLegacyCharPattern = Pattern.compile("(\\\\[&§]([a-f0-9k-or]))");
      mmString = escapedLegacyCharPattern.matcher(mmString).replaceAll((result) -> "&" + result.group(2));
    } else {
      mmString = mmString.replaceAll("([&§][0-9a-fA-Fk-oK-OxXrR])+", "");
    }

    if (!this.gradients) {
      mmString = mmString.replaceAll("(?<!\\\\)<gradient([:#0-9a-fA-F]{8})+>", "");
      mmString = mmString.replaceAll("(?<!\\\\)</gradient>", "");
    }

    final Pattern hexColorPattern = Pattern.compile("(<(/|)(c|color|colour|)(:|)(#[0-9a-fA-F]{6}|)>)");

    if (!this.hexColors) {
      mmString = hexColorPattern.matcher(mmString).replaceAll("");
    }

    // can't use regex, it would mess with placeholders
    if (!this.standardColors) {
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

    private boolean
        gradients,
        hexColors,
        standardColors,
        legacyColors,
        advancedTransformations,
        cssColors;
    private TagResolver placeholderResolver;
    private final Set<TextDecoration> removedTextDecorations;
    private final Set<NamedTextColor> removedColors;

    @ApiStatus.Internal
    BuilderImpl() {
      this.gradients = true;
      this.hexColors = true;
      this.standardColors = true;
      this.legacyColors = false;
      this.advancedTransformations = false;
      this.cssColors = true;
      this.placeholderResolver = TagResolver.empty();
      this.removedTextDecorations = new HashSet<>();
      this.removedColors = new HashSet<>();
    }

    @ApiStatus.Internal
    BuilderImpl(final MiniMessageWrapperImpl wrapper) {
      this.gradients = wrapper.gradients;
      this.hexColors = wrapper.hexColors;
      this.standardColors = wrapper.standardColors;
      this.legacyColors = wrapper.legacyColors;
      this.advancedTransformations = wrapper.advancedTransformations;
      this.cssColors = wrapper.cssColors;
      this.placeholderResolver = wrapper.placeholderResolver;
      this.removedTextDecorations = wrapper.removedTextDecorations;
      this.removedColors = wrapper.removedColors;
    }

    @Override
    public @NotNull Builder gradients(final boolean parse) {
      this.gradients = parse;
      return this;
    }

    @Override
    public @NotNull Builder hexColors(final boolean parse) {
      this.hexColors = parse;
      return this;
    }

    @Override
    public @NotNull Builder standardColors(final boolean parse) {
      this.standardColors = parse;
      return this;
    }

    @Override
    public @NotNull Builder legacyColors(final boolean parse) {
      this.legacyColors = parse;
      return this;
    }

    @Override
    public @NotNull Builder advancedTransformations(final boolean parse) {
      this.advancedTransformations = parse;
      return this;
    }

    @Override
    public @NotNull Builder cssColors(final boolean parse) {
      this.cssColors = parse;
      return this;
    }

    @Override
    public @NotNull Builder removeTextDecorations(final @NotNull TextDecoration... decorations) {
      this.removedTextDecorations.addAll(List.of(decorations));
      return this;
    }

    @Override
    public @NotNull Builder removeTextDecorations(@NotNull Collection<@NotNull TextDecoration> decorations) {
      this.removedTextDecorations.addAll(decorations);
      return this;
    }

    @Override
    public @NotNull Builder placeholderResolver(final @NotNull TagResolver placeholderResolver) {
      this.placeholderResolver = placeholderResolver;
      return this;
    }

    @Override
    public @NotNull Builder removeColors(final @NotNull NamedTextColor... colors) {
      this.removedColors.addAll(List.of(colors));
      return this;
    }

    @Override
    public @NotNull Builder removeColors(@NotNull Collection<@NotNull NamedTextColor> colors) {
      this.removedColors.addAll(colors);
      return this;
    }

    @Override
    public @NotNull MiniMessageWrapper build() {
      return new MiniMessageWrapperImpl(
          this.gradients,
          this.hexColors,
          this.standardColors,
          this.legacyColors,
          this.advancedTransformations,
          this.cssColors,
          this.placeholderResolver,
          this.removedTextDecorations,
          this.removedColors
      );
    }
  }

  private static final Map<Character, String> CHAR_COLORS = new ImmutableMap.Builder<Character, String>()
      .put('0', "<black>")
      .put('1', "<dark_blue>")
      .put('2', "<dark_green>")
      .put('3', "<dark_aqua>")
      .put('4', "<dark_red>")
      .put('5', "<dark_purple>")
      .put('6', "<gold>")
      .put('7', "<gray>")
      .put('8', "<dark_gray>")
      .put('9', "<blue>")
      .put('a', "<green>")
      .put('b', "<aqua>")
      .put('c', "<red>")
      .put('d', "<light_purple>")
      .put('e', "<yellow>")
      .put('f', "<white>")
      .put('k', "<obfuscated>")
      .put('l', "<bold>")
      .put('m', "<strikethrough>")
      .put('n', "<underlined>")
      .put('o', "<italic>")
      .put('r', "<reset>")
      .build();
}
