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
package dev.majek.hexnicks.util;

import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import dev.majek.hexnicks.HexNicks;
import org.jetbrains.annotations.Nullable;

/**
 * Used to check for plugin updates from the Modrinth project page.
 */
public class UpdateChecker {

  private final String currentVersion;
  private String latestVersion;

  /**
   * Construct a new update checker.
   *
   * @param currentVersion The current version of the plugin.
   */
  public UpdateChecker(String currentVersion) {
    this.currentVersion = currentVersion;
    try {
      final String latest = getModrinthVersion();
      this.latestVersion = latest != null ? latest : currentVersion;
    } catch (ExecutionException | InterruptedException ex) {
      HexNicks.logging().error("An error occurred initializing update checker", ex);
      this.latestVersion = currentVersion;
    }
  }

  /**
   * Get the latest plugin version posted on Modrinth.
   */
  private @Nullable String getModrinthVersion() throws ExecutionException, InterruptedException {
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
      try {
        URL url = new URL("https://api.modrinth.com/v2/project/hexnicks/version");
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String str = br.readLine();
        return JsonParser.parseString(str)
            .getAsJsonArray()
            .get(0)
            .getAsJsonObject()
            .get("version_number")
            .getAsString();
      } catch (IOException ex) {
        HexNicks.logging().error("Failed to get latest version from Modrinth API", ex);
        return null;
      }
    });
    return future.get();
  }

  public boolean isLatest() {
    return currentVersion.equals(latestVersion);
  }

  public boolean hasUpdate() {
    return !isLatest();
  }
}
