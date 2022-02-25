/*
 * This file is part of HexNicks, licensed under the MIT License.
 *
 * Copyright (c) 2020-2022 Majekdor
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

import dev.majek.hexnicks.Nicks;
import dev.majek.hexnicks.util.MiniMessageWrapper;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handles hooked plugins.
 */
public class NicksHooks {

  private boolean papiHooked;
  private boolean vaultHooked;
  private boolean essentialsHooked;
  private PapiHook papiHook;
  private VaultHook vaultHook;
  private EssentialsHook essentialsHook;

  public NicksHooks() {
    papiHooked = false;
    vaultHooked = false;
    essentialsHooked = false;
    papiHook = null;
    vaultHook = null;
    essentialsHook = null;
  }

  /**
   * Reload the hooks to make sure we're hooked into all available plugins.
   */
  public void reloadHooks() {
    Nicks.debug("Reloaded hooks...");
    if (Nicks.core().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") &&
        Nicks.core().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
      Nicks.log("Hooking into PlaceholderAPI...");
      papiHooked = true;
      papiHook = new PapiHook(Nicks.core());
      papiHook.register();
    }
    if (Nicks.core().getServer().getPluginManager().isPluginEnabled("Vault") &&
        Nicks.core().getServer().getPluginManager().getPlugin("Vault") != null) {
      Nicks.log("Hooking into Vault...");
      vaultHooked = true;
      vaultHook = new VaultHook();
      // TODO: 12/19/2021 This is a temporary fix for Vault integration sometimes not working despite HexNicks
      // TODO: 12/19/2021 detecting Vault and claiming it hooked.
      if (vaultHook.vaultChat() == null) {
        Nicks.error("Detected Vault and tried to hook but failed.");
        vaultHooked = false;
      }
    }
    if (Nicks.core().getServer().getPluginManager().isPluginEnabled("Essentials") &&
        Nicks.core().getServer().getPluginManager().getPlugin("Essentials") != null) {
      Nicks.log("Hooking into Essentials...");
      essentialsHooked = true;
      essentialsHook = new EssentialsHook();
    }
  }

  /**
   * Check if the plugin is hooked into PlaceholderAPI.
   *
   * @return True if hooked.
   */
  public boolean isPapiHooked() {
    return papiHooked;
  }

  /**
   * Check if the plugin is hooked into Vault.
   *
   * @return True if hooked.
   */
  public boolean isVaultHooked() {
    return vaultHooked;
  }

  /**
   * Check if the plugin is hooked into Essentials.
   *
   * @return True if hooked.
   */
  public boolean isEssentialsHooked() {
    return essentialsHooked;
  }

  /**
   * Apply placeholders from PlaceholderAPI to a string.
   *
   * @param player The player referenced in the string.
   * @param string The string to search.
   * @return Formatted string.
   */
  public @NotNull String applyPlaceHolders(@NotNull Player player, @NotNull String string) {
    return isPapiHooked() ? PapiHook.applyPlaceholders(player, string) : string;
  }

  /**
   * Get a player's Vault prefix if Vault is hooked.
   *
   * @param player The player.
   * @return Player's prefix.
   */
  public @NotNull Component vaultPrefix(@NotNull Player player) {
    return isVaultHooked() ?
        MiniMessageWrapper.legacy().mmParse(vaultHook.vaultChat().getPlayerPrefix(player)) :
        Component.empty();
  }

  /**
   * Get a player's Vault suffix if Vault is hooked.
   *
   * @param player The player.
   * @return Player's suffix.
   */
  public @NotNull Component vaultSuffix(@NotNull Player player) {
    return isVaultHooked() ?
        MiniMessageWrapper.legacy().mmParse(vaultHook.vaultChat().getPlayerSuffix(player)) :
        Component.empty();
  }

  /**
   * Set a player's Essentials nickname if Essentials is hooked.
   *
   * @param player The player.
   * @param nickname The nickname.
   */
  public void setEssNick(@NotNull Player player, @NotNull Component nickname) {
    if (isEssentialsHooked()) {
      essentialsHook.setEssentialsNick(player, nickname);
    }
  }

  /**
   * Get Essentials' nickname prefix if Essentials is hooked.
   *
   * @return Nickname prefix.
   */
  public @Nullable String getEssNickPrefix() {
    if (isEssentialsHooked()) {
      return essentialsHook.getNickPrefix();
    } else {
      return null;
    }
  }
}
