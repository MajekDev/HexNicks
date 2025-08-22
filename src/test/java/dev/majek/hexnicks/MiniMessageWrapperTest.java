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
package dev.majek.hexnicks;

import dev.majek.hexnicks.message.MiniMessageWrapper;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.junit.Assert;
import org.junit.Test;

public class MiniMessageWrapperTest {

  @Test
  public void gradients() {
    String gradient = "<gradient:#1eae98:#d8b5ff>Majekdor</gradient>";
    Assert.assertEquals(
        "Majekdor",
        MiniMessageWrapper.builder().gradients(false).build().mmString(gradient)
    );
  }

  @Test
  public void hexColors() {
    String hex = "<#1eae98>Majek<color:#d8b5ff>dor";
    Assert.assertEquals(
        "<#1eae98>Majek<color:#d8b5ff>dor",
        MiniMessageWrapper.builder().gradients(false).build().mmString(hex)
    );
    Assert.assertEquals(
        "Majekdor",
        MiniMessageWrapper.builder().hexColors(false).build().mmString(hex)
    );
  }

  @Test
  public void standardColors() {
    String color = "<blue>Majek<light_purple>dor";
    Assert.assertEquals(
        "<blue>Majek<light_purple>dor",
        MiniMessageWrapper.builder().hexColors(false).build().mmString(color)
    );
    Assert.assertEquals(
        "Majekdor",
        MiniMessageWrapper.builder().standardColors(false).build().mmString(color)
    );
  }

  @Test
  public void legacyColors() {
    String legacy = "&9&lMajek&b&odor";
    Assert.assertEquals(
        "<blue><bold>Majek<aqua><italic>dor",
        MiniMessageWrapper.legacy().mmString(legacy)
    );
    Assert.assertEquals(
        "Majekdor",
        MiniMessageWrapper.standard().mmString(legacy)
    );
  }

  @Test
  public void legacyObfuscated() {
    String legacy = "&9&kMajekdor";
    Assert.assertEquals(
        "<blue><obfuscated>Majekdor",
        MiniMessageWrapper.legacy().mmString(legacy)
    );
  }

  @Test
  public void legacyHexColors() {
    String legacyHex = "&#336633Majek<blue>dor&a!";
    Assert.assertEquals(
        "<#336633>Majekdor!",
        MiniMessageWrapper.builder().standardColors(false).legacyColors(true).build().mmString(legacyHex)
    );
    Assert.assertEquals(
        "&#336633Majek<blue>dor!",
        MiniMessageWrapper.standard().toBuilder().build().mmString(legacyHex)
    );
    Assert.assertEquals(
        "Majek<blue>dor<green>!",
        MiniMessageWrapper.builder().legacyColors(true).hexColors(false).build().mmString(legacyHex)
    );
  }

  @Test
  public void threeCharHex() {
    String threeCharHex = "&#363Majek<blue>dor";
    Assert.assertEquals(
        "<#336633>Majekdor",
        MiniMessageWrapper.builder().legacyColors(true).standardColors(false).build().mmString(threeCharHex)
    );
  }

  @Test
  public void everything() {
    String everything = "<gradient:#1eae98:#d8b5ff>Majek</gradient><aqua>dor<#336633>!";
    Assert.assertEquals(
        "Majek<aqua>dor<#336633>!",
        MiniMessageWrapper.builder().gradients(false).build().mmString(everything)
    );
    Assert.assertEquals(
        "<gradient:#1eae98:#d8b5ff>Majek</gradient><aqua>dor!",
        MiniMessageWrapper.builder().hexColors(false).build().mmString(everything)
    );
    Assert.assertEquals(
        "Majekdor!",
        MiniMessageWrapper.builder().gradients(false).hexColors(false)
            .standardColors(false).advancedTransformations(false).build().mmString(everything)
    );
    Assert.assertEquals(
        Component.text("Majekdor!"),
        MiniMessageWrapper.builder().gradients(false).hexColors(false).standardColors(false)
            .advancedTransformations(false).build().mmParse(everything)
    );
  }

  @Test
  public void removedDecorations() {
    String string = "<bold><blue>Majekdor";
    Assert.assertEquals(
        MiniMessageWrapper.builder().removeTextDecorations(Set.of(TextDecoration.BOLD)).build().mmParse(string),
        Component.text("Majekdor").color(NamedTextColor.BLUE)
    );
  }

  @Test
  public void placeholderResolver() {
    String string = "<bold><placeholder>Majekdor";
    Assert.assertEquals(
        MiniMessageWrapper.builder().placeholderResolver(TagResolver.resolver(
            Placeholder.parsed("placeholder", "I am ")
        )).build().mmParse(string),
        Component.text("I am Majekdor").decorate(TextDecoration.BOLD)
    );
    string = "<bold><placeholder>Majekdor";
    Assert.assertEquals(
        MiniMessageWrapper.builder().placeholderResolver(TagResolver.resolver(
            Placeholder.parsed("placeholder", "<blue>")
        )).build().mmParse(string),
        Component.text("Majekdor").color(NamedTextColor.BLUE).decorate(TextDecoration.BOLD)
    );
  }

  @Test
  public void removeColors() {
    String string = "<bold><blue>I am <red>Majekdor";
    Assert.assertEquals(
        MiniMessageWrapper.builder().removeColors(NamedTextColor.RED).build().mmParse(string),
        Component.text("I am Majekdor").color(NamedTextColor.BLUE).decorate(TextDecoration.BOLD)
    );
    string = "<bold><blue>I am &cMajekdor";
    Assert.assertEquals(
        MiniMessageWrapper.builder().legacyColors(true).removeColors(NamedTextColor.RED).build().mmParse(string),
        Component.text("I am Majekdor").color(NamedTextColor.BLUE).decorate(TextDecoration.BOLD)
    );
  }

  @Test
  public void singleCssColor() {
    String string = "<aliceblue>This is a test";
    Assert.assertEquals(
        MiniMessageWrapper.standard().mmParse(string),
        Component.text("This is a test", TextColor.color(0xf0f8ff))
    );
  }

  @Test
  public void multipleCssColors() {
    String string = "<aliceblue>Blue</aliceblue> White <orange>Orange";
    Assert.assertEquals(
        MiniMessageWrapper.standard().mmParse(string),
        Component.empty()
            .append(Component.text("Blue").color(TextColor.color(0xf0f8ff)))
            .append(Component.text(" White "))
            .append(Component.text("Orange").color(TextColor.color(0xffa500)))
    );
  }

  @Test
  public void multipleCssColorsUsingArgs() {
    String string = "<css:aliceblue>Blue</css:aliceblue> White <css:orange>Orange";

    Assert.assertEquals(
        MiniMessageWrapper.standard().mmParse(string),
        Component.empty()
            .append(Component.text("Blue").color(TextColor.color(0xf0f8ff)))
            .append(Component.text(" White "))
            .append(Component.text("Orange").color(TextColor.color(0xffa500)))
    );
  }

  @Test
  public void specifyMcColorThatExistsInCss() {
    String string = "<c:aqua>This should be minecraft aqua";

    Assert.assertEquals(
        MiniMessageWrapper.standard().mmParse(string),
        Component.text("This should be minecraft aqua", NamedTextColor.AQUA)
    );
  }

  @Test
  public void cssAndMcColors() {
    String string = "<c:aqua>MC Aqua</c:aqua> White <css:aqua>CSS Aqua</css:aqua>";

    Assert.assertEquals(
        MiniMessageWrapper.standard().mmParse(string), Component.empty()
            .append(Component.text("MC Aqua", NamedTextColor.AQUA))
            .append(Component.text(" White "))
            .append(Component.text("CSS Aqua", TextColor.color(0x00ffff)))
    );
  }
}
