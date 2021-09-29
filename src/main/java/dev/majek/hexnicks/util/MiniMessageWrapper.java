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

package dev.majek.hexnicks.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.transformation.TransformationType;
import net.kyori.adventure.util.Buildable;
import org.jetbrains.annotations.NotNull;

/**
 * A wrapper for {@link MiniMessage} to add a few more methods for more customization.
 *
 * @since 2.1.2
 */
public interface MiniMessageWrapper extends Buildable<MiniMessageWrapper, MiniMessageWrapper.Builder> {

  /**
   * Gets a simple instance with legacy code support.
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
   * @param mmString string to modify with settings from builder
   * @return modified string
   * @since 2.1.2
   */
  @NotNull String mmString(@NotNull String mmString);

  /**
   * Creates a new {@link MiniMessageWrapper.Builder}.
   *
   * @return a builder
   * @since 2.1.2
   */
  static @NotNull Builder builder() {
    return new MiniMessageWrapperImpl.BuilderImpl();
  }

  /**
   * A builder for {@link MiniMessageWrapper}.
   *
   * @since 2.1.2
   */
  interface Builder extends Buildable.Builder<MiniMessageWrapper> {

    /**
     * Whether gradients on the final string should be parsed.
     * Default is true.
     *
     * @param parse whether to parse
     * @return this builder
     * @since 2.1.2
     */
    @NotNull Builder gradients(boolean parse);

    /**
     * Whether hex colors on the final string should be parsed.
     * Default is true.
     *
     * @param parse whether to parse
     * @return this builder
     * @since 2.1.2
     */
    @NotNull Builder hexColors(boolean parse);

    /**
     * Whether all standard color codes on the final string should be parsed.
     * Default is true.
     *
     * @param parse whether to parse
     * @return this builder
     * @since 2.1.2
     */
    @NotNull Builder standardColors(boolean parse);

    /**
     * Whether legacy color codes on the final string should be parsed.
     * Default is false.
     *
     * @param parse whether to parse
     * @return this builder
     * @since 2.1.2
     */
    @NotNull Builder legacyColors(boolean parse);

    /**
     * Whether to parse advanced {@link TransformationType}s on the final string to be parsed.
     * This includes click events, hover events, fonts, etc.
     * Default is false.
     *
     * @param advancedTransformations whether to parse
     * @return this builder
     * @since 2.1.2
     */
    @NotNull Builder advancedTransformations(boolean advancedTransformations);
  }
}