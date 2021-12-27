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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

/**
 * Handles some static utility methods available to tab completer.
 */
public abstract class TabCompleterBase implements TabCompleter {
  /**
   * Returns a list of the currently online players whose name starts with the given partial name.
   *
   * @param partialName the partial name.
   * @return a list of the currently online players whose name starts with the given partial name.
   */
  public static List<String> getOnlinePlayers(String partialName) {
    return filterStartingWith(partialName, Bukkit.getOnlinePlayers().stream().map(Player::getName));
  }

  /**
   * Joins all the arguments after the argument at the given index with the given delimiter.
   *
   * @param index the index.
   * @param delim the delimiter.
   * @param args  the arguments.
   * @return the result of joining the argument after the given index with the given delimiter.
   */
  public static String joinArgsBeyond(int index, String delim, String[] args) {
    ++index;
    String[] data = new String[args.length - index];
    System.arraycopy(args, index, data, 0, data.length);
    return String.join(delim, data);
  }

  /**
   * Filters the given stream by removing null or empty strings, or strings who do not start with the given prefix
   * (ignoring case).
   *
   * @param prefix the prefix to match.
   * @param stream the stream to filter.
   * @return the list of values left after the stream has been filtered.
   */
  public static List<String> filterStartingWith(String prefix, Stream<String> stream) {
    return stream.filter(s -> s != null && !s.isEmpty() && s.toLowerCase().startsWith(prefix.toLowerCase()))
        .collect(Collectors.toList());
  }

  /**
   * Filters the given string list by removing null or empty strings, or strings who do not start with the given
   * prefix (ignoring case). This method is equivalent to calling filterStartingWith(prefix, strings.stream()).
   *
   * @param prefix  the prefix to match.
   * @param strings the strings to filter.
   * @return the list of values left after the strings have been filtered.
   */
  public static List<String> filterStartingWith(String prefix, Collection<String> strings) {
    return filterStartingWith(prefix, strings.stream());
  }
}
