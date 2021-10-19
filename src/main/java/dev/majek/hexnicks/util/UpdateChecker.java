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

import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Used to check for plugin updates from the Spigot plugin page.
 */
public class UpdateChecker {

  private final JavaPlugin plugin;
  private final int resourceId;
  private final int currentVersion;
  private int spigotVersion;

  /**
   * Construct a new update checker.
   *
   * @param plugin The main class of the plugin.
   * @param resourceId Plugin's resource id on Spigot.
   */
  public UpdateChecker(JavaPlugin plugin, int resourceId) {
    this.plugin = plugin;
    this.resourceId = resourceId;
    this.currentVersion = Integer.parseInt(plugin.getDescription().getVersion()
        .replace(".", "").replace("-SNAPSHOT", ""));
    try {
      this.spigotVersion = Integer.parseInt(getSpigotVersion().replace(".", ""));
    } catch (ExecutionException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get the plugin version currently posted on Spigot.
   */
  private String getSpigotVersion() throws ExecutionException, InterruptedException {
    CompletableFuture<String> spigotVersion = CompletableFuture.supplyAsync(() -> {
      try {
        URL url = new URL("https://api.spigotmc.org/simple/0.1/index.php?action=getResource&id=" + resourceId);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String str = br.readLine();
        return JsonParser.parseString(str).getAsJsonObject().get("current_version").getAsString();
      } catch (IOException exception) {
        this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
        return null;
      }
    });
    return spigotVersion.get();
  }

  public boolean isAheadOfSpigot() {
    return currentVersion > spigotVersion;
  }

  public boolean isBehindSpigot() {
    return spigotVersion > currentVersion;
  }

  public boolean isSpigotLatest() {
    return spigotVersion == currentVersion;
  }
}