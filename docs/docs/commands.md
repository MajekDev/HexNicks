---
id: commands
title: Commands
slug: /commands
---

The main plugin command you'll be using is `/nick` to change your nickname, but all of the commands in the plugin are 
explained below. This page will reference color codes a lot, you can read about color 
formatting [here](https://hexnicks.majek.dev/color-formatting).

## /nick

Usage: `/nick <nickname>`

This is the command players will use to change their nickname. It requires one argument (the nickname) and unless 
you disable `require-alphanumeric` in the 
[config](https://hexnicks.majek.dev/config-options#Require-Alphanumeric) 
then the nickname may only contain valid color codes, letters, and numbers. If you want nicknames with spaces, special 
characters, etc. then go ahead and disable that config option.

## /nickother

Usage: `/nickother <player> <nickname>`

This is a staff command for changing the nicknames of other players. It requires two arguments, the name of the 
player whose nickname to change and the nickname itself. The target player must be online in order to change their 
nickname. This command follows the same rules as `/nick` (see above) in regards to alphanumeric nicknames.

In earlier versions of the plugin, you could use a player name as an argument in `/nick` to change another player's 
nickname. This was confusing and annoying for nicknames with spaces, so changing another player's nickname has been 
broken out into its own command.

## /nonick

Usage: `/nonick [player]`

This command is used to remove either your (the player running the command) or another player's nickname 
(if you're staff). This will set the player's display name back to the player's username and remove 
the nickname from storage.

## /nickcolor

Usage: `/nickcolor <color>`

This is an interesting command. It was actually added due to a user request. This command will allow you to 
change just the color of your nickname, so it only accepts valid color codes. If you include any additional 
characters the command will not work. You would use this command in a situation where, for example, your 
nickname is red and you want to keep the same text but make the color blue.

## /realname

Usage: `/realname <nickname>`

This command will return the username of the specified nickname. It requires one argument, the nickname, 
and will try to match that nickname to a player. It will tab complete the nicknames of all online players and 
if the plugin is running on Paper, you can hover on the tab completed nickname to view it with colors.

## /hexnicks

Usage: `/hexnicks <reload|config-editor|latest-log> [new|apply] [link]`

This staff command has multiple subcommands used for managing the plugin.

### /hexnicks reload

Usage: `/hexnicks reload`

This will reload the plugin's config files, storage, and plugin hooks. Use this if you want to apply config file changes.

:::note
This is already called on `/hexnicks config-editor apply <link>`
:::

### /hexnicks config-editor

Usage: `/hexnicks config-editor <apply|new> [link]`

This will either create a new link to the config file in pastebin or apply changes from a config file in pastebin. 

**How to use:**

1. First, execute `/hexnicks config-editor new` to create a new pastebin with your config. The command will give you the link.
2. Then, make all your desired changes to the config in the pastebin.
3. Make sure to press save in the upper left corner of the pastebin. You will get a new link. (It should copy to your clipboard automatically)
4. Take your new link and execute `/hexnicks config-editor apply <link>` to apply your changes.
5. That's it! The plugin will automatically reload, and you're good to go.

### /hexnicks latest-log

Usage: `/hexnicks latest-log`

This will fetch the most recent log file from HexNicks' logs folder and send you a link to view it in pastebin. 
Logs are kept for each day dating back one week. Old logs are automatically deleted.