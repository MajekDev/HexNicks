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
import org.junit.Assert;
import org.junit.Test;

public class MiniMessageWrapperTest {

  @Test
  public void main() {
    String gradient = "<gradient:#1eae98:#d8b5ff>Majekdor</gradient>";
    Assert.assertEquals("Majekdor",
        MiniMessageWrapper.builder().gradients(false).build().mmString(gradient));

    String color = "<blue>Majek<light_purple>dor";
    Assert.assertEquals("<blue>Majek<light_purple>dor",
        MiniMessageWrapper.builder().hexColors(false).build().mmString(color));
    Assert.assertEquals("Majekdor",
        MiniMessageWrapper.builder().standardColors(false).build().mmString(color));

    String hex = "<#1eae98>Majek<color:#d8b5ff>dor";
    Assert.assertEquals("<#1eae98>Majek<color:#d8b5ff>dor",
        MiniMessageWrapper.builder().gradients(false).build().mmString(hex));
    Assert.assertEquals("Majekdor",
        MiniMessageWrapper.builder().hexColors(false).build().mmString(hex));

    String everything = "<gradient:#1eae98:#d8b5ff>Majek</gradient><aqua>dor<#336633>!";
    Assert.assertEquals("Majek<aqua>dor<#336633>!",
        MiniMessageWrapper.builder().gradients(false).build().mmString(everything));
    Assert.assertEquals("<gradient:#1eae98:#d8b5ff>Majek</gradient><aqua>dor!",
        MiniMessageWrapper.builder().hexColors(false).build().mmString(everything));
    Assert.assertEquals("Majekdor!",
        MiniMessageWrapper.builder().gradients(false).hexColors(false)
            .standardColors(false).build().mmString(everything));

    // TODO: 9/29/2021 add legacy and advanced transformation tests
  }
}