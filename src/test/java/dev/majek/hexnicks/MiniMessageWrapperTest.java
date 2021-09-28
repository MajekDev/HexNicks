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

package dev.majek.hexnicks;

import dev.majek.hexnicks.util.MiniMessageWrapper;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.junit.Assert;
import org.junit.Test;

public class MiniMessageWrapperTest {

  @Test
  public void main() {
    LegacyComponentSerializer serializer = LegacyComponentSerializer.builder()
        .useUnusualXRepeatedCharacterHexFormat().hexColors().build();

    String gradient = "<gradient:#1eae98:#d8b5ff>Majekdor</gradient>";
    MiniMessageWrapper gradientWrapper = new MiniMessageWrapper(gradient);
    Assert.assertEquals("Majekdor", serializer.serialize(gradientWrapper.removeGradients().mmParse()));

    String color = "<blue>Majek<aqua>dor";
    MiniMessageWrapper colorWrapper = new MiniMessageWrapper(color);
    Assert.assertEquals("§9Majek§bdor", serializer.serialize(colorWrapper.removeHex().mmParse()));
    Assert.assertEquals("Majekdor", serializer.serialize(colorWrapper.removeAllTokens().mmParse()));

    String hex = "<#1eae98>Majek<color:#d8b5ff>dor";
    Assert.assertEquals("§x§1§e§a§e§9§8Majek§x§d§8§b§5§f§fdor",
        serializer.serialize(new MiniMessageWrapper(hex).removeGradients().mmParse()));
    Assert.assertEquals("Majekdor", serializer.serialize(new MiniMessageWrapper(hex).removeHex().mmParse()));

    String everything = "<gradient:#1eae98:#d8b5ff>Majek</gradient><aqua>dor<#336633>!";
    Assert.assertEquals("Majek§bdor§x§3§3§6§6§3§3!",
        serializer.serialize(new MiniMessageWrapper(everything).removeGradients().mmParse()));
    Assert.assertEquals("§x§1§e§a§e§9§8M§x§4§3§a§f§a§da§x§6§8§b§1§c§1j§x§8§e§b§2§d§6e§x§b§3§b§4§e§ak§bdor!",
        serializer.serialize(new MiniMessageWrapper(everything).removeHex().mmParse()));
    Assert.assertEquals("Majekdor!", serializer.serialize(new MiniMessageWrapper(everything)
        .removeAllTokens().mmParse()));
  }
}