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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/**
 * A wrapper for {@link MiniMessage} to add a few more methods for more customization.
 *
 * @since 2.1.2
 */
public interface MiniMessageWrapper {

  /**
   * <p>Gets a simple instance.</p>
   * <p>This will parse everything like {@link MiniMessage} will except for advanced transformations.</p>
   * <p>Builder options with this instance:</p>
   * <ul>
   *   <li>Gradients: True</li>
   *   <li>Hex Colors: True</li>
   *   <li>Standard Colors: True</li>
   *   <li>Legacy Colors: False</li>
   *   <li>Advanced Transformations: False</li>
   * </ul>
   *
   * @return a simple instance
   * @since 2.1.2
   */
  static @NotNull MiniMessageWrapper standard() {
    return MiniMessageWrapperImpl.STANDARD;
  }

  /**
   * <p>Gets a simple instance with legacy code support.</p>
   * <p>This will parse everything like {@link MiniMessage} will with the addition of
   * legacy code support and the subtraction of advanced transformation support.</p>
   * <p>Builder options with this instance:</p>
   * <ul>
   *   <li>Gradients: True</li>
   *   <li>Hex Colors: True</li>
   *   <li>Standard Colors: True</li>
   *   <li>Legacy Colors: True</li>
   *   <li>Advanced Transformations: False</li>
   * </ul>
   *
   * @return a simple instance
   * @since 2.1.2
   */
  static @NotNull MiniMessageWrapper legacy() {
    return MiniMessageWrapperImpl.LEGACY;
  }

  /**
   * Parse a string into a {@link Component} using {@link MiniMessage}.
   *
   * @param mmString the string to parse
   * @return component
   * @since 2.1.2
   */
  @NotNull Component mmParse(@NotNull String mmString);

  /**
   * Get the modified string.
   *
   * @param mmString string to modify
   * @return modified string
   * @since 2.1.2
   */
  @NotNull String mmString(@NotNull String mmString);

  /**
   * <p>Creates a new {@link Builder}.</p>
   * <p>Default builder options:</p>
   * <ul>
   *   <li>Gradients: True</li>
   *   <li>Hex Colors: True</li>
   *   <li>Standard Colors: True</li>
   *   <li>Legacy Colors: False</li>
   *   <li>Advanced Transformations: False</li>
   * </ul>
   *
   * @return a builder
   * @since 2.1.2
   */
  static @NotNull Builder builder() {
    return new MiniMessageWrapperImpl.BuilderImpl();
  }

  /**
   * Create a {@link Builder} to modify options.
   *
   * @return a builder
   * @since 2.1.2
   */
  @NotNull Builder toBuilder();

  /**
   * A builder for {@link MiniMessageWrapper}.
   *
   * @since 2.1.2
   */
  interface Builder {

    /**
     * Whether gradients on the final string should be parsed.
     *
     * @param parse whether to parse
     * @return this builder
     * @since 2.1.2
     */
    @NotNull Builder gradients(final boolean parse);

    /**
     * Whether hex colors on the final string should be parsed.
     *
     * @param parse whether to parse
     * @return this builder
     * @since 2.1.2
     */
    @NotNull Builder hexColors(final boolean parse);

    /**
     * Whether all standard color codes on the final string should be parsed.
     *
     * @param parse whether to parse
     * @return this builder
     * @since 2.1.2
     */
    @NotNull Builder standardColors(final boolean parse);

    /**
     * Whether legacy color codes on the final string should be parsed.
     *
     * @param parse whether to parse
     * @return this builder
     * @since 2.1.2
     */
    @NotNull Builder legacyColors(final boolean parse);

    /**
     * Whether to parse advanced {@link Tag}s on the final string to be parsed.
     * This includes click events, hover events, fonts, etc.
     *
     * @param parse whether to parse
     * @return this builder
     * @since 2.1.2
     */
    @NotNull Builder advancedTransformations(final boolean parse);

    /**
     * Whether CSS color codes on the final string should be parsed.
     *
     * @param parse whether to parse
     * @return this builder
     * @since 3.2.0
     */
    @NotNull Builder cssColors(final boolean parse);

    /**
     * The {@link TextDecoration}s that should not be parsed.
     *
     * @param decorations the decorations
     * @return this builder
     * @since 2.2.0
     */
    @NotNull Builder removeTextDecorations(final @NotNull TextDecoration... decorations);

    /**
     * The {@link TextDecoration}s that should not be parsed.
     *
     * @param decorations the decorations
     * @return this builder
     * @since 3.0.0
     */
    @NotNull Builder removeTextDecorations(final @NotNull Collection<@NotNull TextDecoration> decorations);

    /**
     * Set the {@link TagResolver} for the {@link MiniMessage} instance.
     *
     * @param placeholderResolver the placeholder resolver
     * @return this builder
     * @since 2.2.1
     */
    @NotNull Builder placeholderResolver(final @NotNull TagResolver placeholderResolver);

    /**
     * The {@link NamedTextColor}s that should not be parsed.
     *
     * @param colors the colors
     * @return this builder
     * @since 2.2.1
     */
    @NotNull Builder removeColors(final @NotNull NamedTextColor... colors);

    /**
     * The {@link NamedTextColor}s that should not be parsed.
     *
     * @param colors the colors
     * @return this builder
     * @since 3.0.0
     */
    @NotNull Builder removeColors(final @NotNull Collection<@NotNull NamedTextColor> colors);

    /**
     * Build the {@link MiniMessageWrapper} ready to parse.
     *
     * @return the wrapper
     * @since 2.1.2
     */
    @NotNull MiniMessageWrapper build();
  }
}
