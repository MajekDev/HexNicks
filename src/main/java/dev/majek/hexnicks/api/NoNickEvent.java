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
package dev.majek.hexnicks.api;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Handles the event fired when a player removes their nickname.
 */
public class NoNickEvent extends Event implements Cancellable {

  private static final HandlerList HANDLER_LIST = new HandlerList();
  private final Player player;
  private final Component oldNick;
  private boolean canceled;

  /**
   * Fires when a player removes their nickname using <code>/nonick</code>.
   *
   * @param player  The in-game player changing the nickname.
   * @param oldNick The player's old nickname being removed.
   */
  public NoNickEvent(@NotNull Player player, @NotNull Component oldNick) {
    this.player = player;
    this.oldNick = oldNick;
    this.canceled = false;
  }

  /**
   * The in-game player attempting to change the nickname.
   *
   * @return Player.
   */
  public Player player() {
    return player;
  }

  /**
   * The old nickname being removed.
   *
   * @return Old nickname.
   */
  @NotNull
  public Component oldNick() {
    return oldNick;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isCancelled() {
    return canceled;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCancelled(boolean cancel) {
    this.canceled = cancel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  /**
   * Get the HandlerList. Bukkit requires this.
   *
   * @return HandlerList.
   */
  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }
}
