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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages plugin logging. This includes printing log message, saving to file,
 * and uploading to <a href="https://pastes.dev">pastes.dev</a>.
 */
public class LoggingManager {

  private final Plugin plugin;
  private final Logger logger;
  private final File directory;
  private boolean debug;

  /**
   * Create a new logging manager.
   *
   * @param plugin the plugin for which logging will be managed
   * @param directory the directory to store logs in
   *                  set this to null if log files shouldn't be kept
   */
  public LoggingManager(final @NotNull Plugin plugin, final @Nullable File directory) {
    this.plugin = plugin;
    this.logger = plugin.getLogger();
    this.directory = directory;
    this.debug = false;
  }

  /**
   * Log a simple message.
   *
   * @param message the message
   */
  public void log(final @NotNull String message) {
    this.logger.log(Level.INFO, message);
    this.writeToFile(Level.INFO, message);
  }

  /**
   * Log an error. This should be something severe.
   *
   * @param message the error message
   */
  public void error(final @NotNull String message) {
    this.logger.log(Level.SEVERE, message);
    this.writeToFile(Level.SEVERE, message);
  }

  /**
   * Log an error and it's exception.
   *
   * @param message the error message
   * @param throwable the exception
   */
  public void error(final @NotNull String message, final @NotNull Throwable throwable) {
    this.error(message);
    this.error(throwable.getMessage());
    this.writeToFile(Level.SEVERE, throwable.getMessage());
  }

  /**
   * Log a debug message. This will only log if {@link #debug} is set to true.
   *
   * @param message the message
   */
  public void debug(final @NotNull String message) {
    if (this.debug) {
      this.logger.log(Level.WARNING, message);
      this.writeToFile(Level.WARNING, message);
    }
  }

  /**
   * Log a debug message and send it to a user. This will only log if {@link #debug} is set to true.
   *
   * @param message the message
   * @param audience the user to send the message to
   */
  public void debug(final @NotNull String message, final @NotNull Audience audience) {
    if (this.debug) {
      this.debug(message);
      audience.sendMessage(Component.text("[DEBUG] [" + this.plugin.getDescription().getName() + "] " + message));
    }
  }

  /**
   * Publish the latest log to <a href="https://pastes.dev">pastes.dev</a>.
   *
   * @return the link to the log on the site
   */
  public @NotNull String latestToPasteBin() {
    final HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://bytebin.majek.dev/post"))
        .header("User-Agent", "hexnicks")
        .header("Content-Type", "text/plain; charset=utf-8")
        .POST(HttpRequest.BodyPublishers.ofString(this.latestLog()))
        .build();

    final JsonObject json = JsonParser.parseString(MiscUtils.sendRequestAndGetResponse(request)).getAsJsonObject();
    final String url = "https://paste.majek.dev/" + json.get("key").getAsString();
    this.log("Uploaded latest log to " + url);

    return url;
  }

  /**
   * Get the latest log from the latest file.
   *
   * @return the latest log
   */
  public @NotNull String latestLog() {
    StringBuilder contentBuilder = new StringBuilder();
    contentBuilder.append("Log file for ").append(this.plugin.getDescription().getName()).append("\n")
        .append("Submitted at ").append(LocalDate.now()).append(" on ")
        .append(new SimpleDateFormat("HH:mm:ss").format(new Date())).append("\n")
        .append("Server Software: ").append(Bukkit.getVersion()).append("\n")
        .append("Plugin Version: ").append(this.plugin.getDescription().getVersion())
        .append("\n\n");
    try (Stream<String> stream = Files.lines(Paths.get(this.latestLogFile().toURI()), StandardCharsets.UTF_8)) {
      stream.forEach(s -> contentBuilder.append(s).append("\n"));
    } catch (final IOException ex) {
      this.error("Error getting latest log from file", ex);
      return "Error getting latest log from file.";
    }
    contentBuilder.append("\nEnd of file.");
    return contentBuilder.toString();
  }

  /**
   * Get the latest log file being used.
   *
   * @return the latest log file
   */
  public File latestLogFile() {
    return new File(this.directory, LocalDate.now() + ".txt");
  }

  /**
   * Whether debugging message should be sent.
   *
   * @return debugging status
   */
  public boolean doDebug() {
    return debug;
  }

  /**
   * Set whether to send debugging messages.
   *
   * @param debug debugging status
   */
  public void doDebug(final boolean debug) {
    this.debug = debug;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private void writeToFile(final @NotNull Level level, @NotNull String message) {
    if (this.directory == null) {
      return;
    }

    String levelName = level.getName();
    if (level == Level.WARNING) {
      levelName = "DEBUG";
    }

    message = "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + " " + levelName + "] " + message;
    final File logFile = new File(this.directory, LocalDate.now() + ".txt");

    try {
      if (!this.directory.exists())
        this.directory.mkdirs();
      if (!logFile.exists())
        logFile.createNewFile();

      final PrintWriter writer = new PrintWriter(new FileWriter(logFile, true));
      writer.println(message);
      writer.flush();
      writer.close();

      final File[] logFiles = this.directory.listFiles();
      long oldestDate = Long.MAX_VALUE;
      File oldestFile = null;
      if (logFiles != null && logFiles.length > 7) {
        for (File f : logFiles)
          if (f.lastModified() < oldestDate) {
            oldestDate = f.lastModified();
            oldestFile = f;
          }
        if (oldestFile != null) {
          oldestFile.delete();
          this.log("Deleting 1 week+ old file " + oldestFile.getName());
        }
      }
    } catch (final IOException ex) {
      this.error("Failure writing log to file", ex);
    }
  }
}
