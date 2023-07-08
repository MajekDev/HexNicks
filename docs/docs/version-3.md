---
id: version-3
title: Version 3
slug: /v3
---

# HexNicks Version 3.0.0
Version 3.0.0 officially supports Paper 1.18.2 and above. No version of Spigot is supported and the plugin will not even work on Spigot servers.
## Changes

### Config File Editing
You are now able to edit the plugin config file in your browser. Steps:
1. Type `/hexnicks config-editor new` to get a link to the config file.
2. Follow the link and make all of your changes.
3. Save the file in paste bin and copy the link.
4. Type `/hexnicks config-editor apply <link>`, replacing `<link>` with the link you copied.
5. That's it! Your config file is updated and reloaded automatically.

### New Placeholders
Because of a desire for different formats of text returned by placeholders, there are now 3 additional [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI) placeholders.
- `%hexnicks_nick%` - Returns the same as before: the player's nickname with legacy formatting.
- `%hexnicks_nick_raw%` - Returns the player's nickname with no formatting at all.
- `%hexnicks_nick_hex%` - Returns the player's nickname with hex formatting used by [DeluxeChat](https://www.spigotmc.org/resources/deluxechat.1277/) (and derivatives like [ChitChat](https://github.com/heychazza/ChitChat).
- `%hexnicks_nick_mm%` - Returns the player's nickname with MiniMessage formatting.

### Added Logging
HexNicks now has the ability to log important plugin information to a file. This can be incredibly useful for the developer when an error occurs. You can view the latest log with `/hexnicks latest-log`.

HexNicks also keeps one week worth of logs stored as files, but automatically deletes anything older. You can find them in the `logs` folder inside the main plugin folder.

### Blocking Text Decorations With Permissions
The config option for blocking decoration codes (things like bold and italic) has been removed. Instead those are now permissions in the same way blocking color codes is done with permissions.

All players are given permission for all decorations by default, but you can block formatting access with the permission node `hexnicks.decoration.<decoration>`, replacing `<decoration>` with the decoration you wish to block (bold, italic, etc.).

> Read more in the updated **permissions** page in the [docs](https://hexnicks.majek.dev/permissions).

### Removed Deprecated Methods
The old methods that returned a nickname from storage _not_ enclosed in a [CompletableFuture](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html) have been removed. You must use the CompletableFutures to deal with nickname being pulled from a file/database.

### Classes Renamed
A lot of classes were renamed to things that I believe make more sense.
- The main class `Nicks` has been renamed to `HexNicks`.
- The utility class `NicksUtils` has been renamed to `MiscUtils` and it now a proper static utility class.
- The class that handles SQL connections `NicksSql` has been renamed to `SqlManager`.
- The hook manager class `NicksHooks` has been renamed to `HookManager`.
- The class that stores config values `NicksConfig` has been renamed to `ConfigValues`.
- The API class `NicksApi` has been renamed to `HexNicksApi`.
- `MiniMessageWrapper` and `MiniMessageWrapperImpl` have also moved to a new package `message`.

### Dependencies
Due to only supporting Paper, a lot of dependencies have been dropped as they are included in Paper. All [adventure](https://github.com/KyoriPowered/adventure) (including MiniMessage) and Google (Guava and Gson) dependencies have been removed.

### Config Option For `/nickother`
There is a new config option, `nickother-override`, which, when enabled, allows players who have the ability to use `/nickother` to override the target player's permissions for formatting codes.

This means you can now give someone a nickname using the color aqua even if they don't have permission to use that color.

If the config option is disabled then `/nickother` will obey the target player's permissions for formatting.

### Reload Command Changed
To reload the plugin in version 2.x.x you would use `/nicksreload`. This has been moved to `/hexnicks reload`. It has the same functionality, just different execution.

## Support
If you find any issues with version 3.0.0 then please either open an issue right here on GitHub or join my [Discord](https://discord.majek.dev)!
