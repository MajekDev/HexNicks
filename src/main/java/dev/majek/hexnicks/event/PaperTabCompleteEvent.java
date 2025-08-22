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
package dev.majek.hexnicks.event;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import dev.majek.hexnicks.HexNicks;
import java.util.stream.Collectors;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * Handles tab completion for <code>/realname</code>.
 */
public class PaperTabCompleteEvent implements Listener {

  @EventHandler
  public void onTabComplete(AsyncTabCompleteEvent event) {
    String[] args = event.getBuffer().split(" ");
    if (!event.isCommand()) {
      return;
    }
    if (args[0].contains("realname")) {
      nickCompletions(event, args);
    }
  }

  public void nickCompletions(@NotNull AsyncTabCompleteEvent event, @NotNull String[] args) {
    if (args.length > 1) {
      event.completions(
          HexNicks.core().getNickMap().values()
              .stream()
              .filter(nickname ->
                  PlainTextComponentSerializer.plainText().serialize(nickname).startsWith(args[1])
              )
              .map(nickname ->
                  AsyncTabCompleteEvent.Completion.completion(
                      PlainTextComponentSerializer.plainText().serialize(nickname), nickname)
              )
              .collect(Collectors.toList())
      );
    } else {
      event.completions(
          HexNicks.core().getNickMap().values()
              .stream()
              .map(nickname ->
                  AsyncTabCompleteEvent.Completion.completion(
                      PlainTextComponentSerializer.plainText().serialize(nickname), nickname)
              )
              .collect(Collectors.toList())
      );
    }
  }
}
