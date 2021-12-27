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
package dev.majek.hexnicks.hook;

import com.earth2me.essentials.Essentials;
import dev.majek.hexnicks.Nicks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handles Essentials hook methods.
 */
class EssentialsHook {

  private final Essentials essentials;
  private final LegacyComponentSerializer legacyComponentSerializer;

  public EssentialsHook() {
    this.essentials = (Essentials) Nicks.core().getServer().getPluginManager().getPlugin("Essentials");
    legacyComponentSerializer = LegacyComponentSerializer.builder().hexColors()
        .useUnusualXRepeatedCharacterHexFormat().build();
  }

  /**
   * Set a player's Essentials nickname.
   *
   * @param player The player.
   * @param nickname The nickname.
   */
  public void setEssentialsNick(@NotNull Player player, @NotNull Component nickname) {
    if (Nicks.config().OVERRIDE_ESSENTIALS) {
      essentials.getUser(player).setNickname(legacyComponentSerializer.serialize(nickname));
    }
  }

  /**
   * Get Essentials' nickname prefix.
   *
   * @return Nickname prefix.
   */
  public @Nullable String getNickPrefix() {
    return essentials.getConfig().getString("nickname-prefix");
  }
}
