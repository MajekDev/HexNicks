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
import org.jetbrains.annotations.NotNull;

public class MiniMessageWrapper {

  private String mmString;

  /**
   * Create a new wrapper with a string with {@link MiniMessage} tags to eventually be parsed.
   *
   * @param mmString String with {@link MiniMessage} tags.
   */
  public MiniMessageWrapper(@NotNull String mmString) {
    this.mmString = mmString;
  }

  /**
   * Remove {@link MiniMessage}'s hex color code tokens.
   *
   * @return Wrapper.
   */
  public MiniMessageWrapper removeHex() {
    this.mmString = this.mmString.replaceAll("<#([0-9a-fA-F]{6})>", "");
    this.mmString = this.mmString.replaceAll("</#([0-9a-fA-F]{6})>", "");
    this.mmString = this.mmString.replaceAll("<c:#([0-9a-fA-F]{6})>", "");
    this.mmString = this.mmString.replaceAll("</c:#([0-9a-fA-F]{6})>", "");
    this.mmString = this.mmString.replaceAll("</c>", "");
    this.mmString = this.mmString.replaceAll("<color:#([0-9a-fA-F]{6})>", "");
    this.mmString = this.mmString.replaceAll("</color:#([0-9a-fA-F]{6})>", "");
    this.mmString = this.mmString.replaceAll("</color>", "");
    this.mmString = this.mmString.replaceAll("<colour:#([0-9a-fA-F]{6})>", "");
    this.mmString = this.mmString.replaceAll("</colour:#([0-9a-fA-F]{6})>", "");
    this.mmString = this.mmString.replaceAll("</colour>", "");
    return this;
  }

  /**
   * Remove {@link MiniMessage}'s gradient tokens.
   *
   * @return Wrapper.
   */
  public MiniMessageWrapper removeGradients() {
    this.mmString = this.mmString.replaceAll("<gradient([:#0-9a-fA-F]{8})+>", "");
    this.mmString = this.mmString.replaceAll("</gradient>", "");
    return this;
  }

  /**
   * Remove all of {@link MiniMessage}'s tokens.
   *
   * @return Wrapper.
   */
  public MiniMessageWrapper removeAllTokens() {
    this.mmString = MiniMessage.get().stripTokens(mmString);
    return this;
  }

  /**
   * Parse the string passed through in the constructor with {@link MiniMessage}.
   *
   * @return Parsed {@link Component}.
   */
  public Component mmParse() {
    return MiniMessage.get().parse(mmString);
  }

  /**
   * Return the modified string passed through in the constructor.
   *
   * @return Modified string.
   */
  public String mmString() {
    return mmString;
  }
}