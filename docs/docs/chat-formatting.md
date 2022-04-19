---
id: chat-formatting
title: Chat Formatting
slug: /chat-formatting
---

The built-in chat formatter allows HexNicks to control how your chat is formatted. If you would rather use a 
different plugin to format your chat then HexNicks' chat formatter can always be disabled in the config. Modify 
the chat format to your liking in the `chat-format` field of the config 
[here](https://github.com/MajekDev/HexNicks/blob/57e09bd43cfd9df96d74263f192dea6c03f3fe6f/src/main/resources/config.yml#L22).

> Read about the config options [here](https://github.com/MajekDev/HexNicks/wiki/Configuration-Options).

By default, the chat format is the same as vanilla Minecraft, with angle brackets around the display name (nickname). 
This can be changed to whatever format you like, but please remember to maintain correct YAML syntax. Use a syntax checker 
[here](https://yamlchecker.com/).

> Note: A typical problem is forgetting to wrap the format in quotations (""). If it's not working make sure you try this before asking for support.

## Placeholders

There are two placeholders you must include, `{displayname}`, which will be replaced by the player's display name 
(nickname), and `{message}`, which will be replaced by the chat message.

There are also two additional placeholders, `{prefix}` and `{suffix}`, which will be replaced by the player's 
[Vault](https://github.com/milkbowl/Vault) prefix and/or suffix assuming Vault is hooked and running on the server.

You may also include placeholders from [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI),
and they will be parsed.

HexNicks has a few PlaceHolderAPI placeholders:
- `%hexnicks_nick%` - Returns the player's nickname with legacy formatting.
- `%hexnicks_nick_raw%` - Returns the player's nickname with no formatting at all.
- `%hexnicks_nick_hex%` - Returns the player's nickname with hex formatting used by 
[DeluxeChat](https://www.spigotmc.org/resources/deluxechat.1277/) (and derivatives like [ChitChat](https://github.com/heychazza/ChitChat)).
- `%hexnicks_nick_mm%` - Returns the player's nickname with MiniMessage formatting.