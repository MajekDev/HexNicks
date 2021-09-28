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

import dev.majek.hexnicks.Nicks;
import org.bukkit.entity.Player;

/**
 * Handles hooked plugins.
 */
public class NicksHooks {

  private boolean papiHooked;
  private boolean vaultHooked;
  private PapiHook papiHook;
  private VaultHook vaultHook;

  public NicksHooks() {
    papiHooked = false;
    vaultHooked = false;
    papiHook = null;
    vaultHook = null;
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
   * Apply placeholders from PlaceholderAPI to a string.
   *
   * @param player The player referenced in the string.
   * @param string The string to search.
   * @return Formatted string.
   */
  public String applyPlaceHolders(Player player, String string) {
    return isPapiHooked() ? PapiHook.applyPlaceholders(player, string) : string;
  }

  public String vaultPrefix(Player player) {
    return isVaultHooked() ? Nicks.utils().applyLegacyColors(vaultHook.vaultChat().getPlayerPrefix(player)) : "";
  }

  public String vaultSuffix(Player player) {
    return isVaultHooked() ? Nicks.utils().applyLegacyColors(vaultHook.vaultChat().getPlayerSuffix(player)) : "";
  }
}