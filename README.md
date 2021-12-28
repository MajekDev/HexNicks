<!--[![publish](https://github.com/Majekdor/HexNicks/actions/workflows/publish.yml/badge.svg)](https://repo.majek.dev/releases/dev/majek/hexnicks/HexNicks)-->
[![build](https://github.com/MajekDev/HexNicks/actions/workflows/build.yml/badge.svg)](https://github.com/MajekDev/HexNicks/actions/workflows/build.yml)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/MajekDev/HexNicks)](https://github.com/MajekDev/HexNicks/releases/latest)
[![chat](https://img.shields.io/discord/753727849860432076?color=%237289da)](https://discord.majek.dev)
[![](https://img.shields.io/spiget/rating/83554?color=%23ff781f&label=Spigot)](https://www.spigotmc.org/resources/83554/)
<img align="right" src="https://raw.githubusercontent.com/MajekDev/HexNicks/main/hexnicks.png" height="250" width="250">

# HexNicks

HexNicks is a simple nickname plugin that allows players to set their nickname to anything they like containing normal colors, hex colors, and even gradients! It fully supports Spigot and Paper, though forks of those should work as well. There are only a few commands and permissions to limit what players use what commands. Though the plugin does have support for MySQL storage, it's mostly intended to be a simple nickname plugin for smaller Spigot/Paper servers.

## Features

- Parsing via [MiniMessage](https://docs.adventure.kyori.net/minimessage.html) - here's a [handy website](https://webui.adventure.kyori.net/) to pracitce.
- Optimized support for Spigot and Paper. Forks of either should work as well.
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) support with the placeholder `%hexnicks_nick%` for player's nicknames.
- MySQL storage support for users on BungeeCord/Velocity.
- Metrics through [bStats](https://bstats.org/plugin/bukkit/HexNicks/8764).
- Active plugin and developer.
- Ability to format chat on it's own, no external chat formatter needed.
- Fully-featured [api](https://github.com/MajekDev/HexNicks/tree/main/src/main/java/dev/majek/hexnicks/api) for developers.
- Full documentation [here](https://github.com/MajekDev/HexNicks/wiki).

## Commands

There are 6 plugin commands:
- `/nick <nickname>` - Set your own nickname.
- `/nickother <player> <nickname>` - Set another player's nickname.
- `/nonick [player]` - Remove your nickname or another player's nickname.
- `/nickcolor <color>` - Change the color of your nickname.
- `/realname <nickname>` - Get the username of the player with a specific nickname.
- `/nicksreload` - Reload the plugin.

## Permissions

All permissions except `hexnicks.nick.other`, `hexnicks.nonick.other`, `hexnicks.chat.advanced`, and `hexnicks.reload` are given to all players by default but can be negated by a permissions manager like [LuckPerms](https://luckperms.net/).
- `hexnicks.nick` - Permission to change your own nickname.
- `hexnicks.nick.color` - Permission to use standard color codes in nicknames.
- `hexnicks.nick.hex` - Permission to use hex color codes in nicknames.
- `hexnicks.nick.gradient` - Permission to use gradients in nicknames.
- `hexnicks.nick.other` - Permissions to change other player's nicknames.
- `hexnicks.nonick` - Permission to remove your own nickname.
- `hexnicks.nonick.other` - Permission to remove other player's nicknames.
- `hexnicks.nickcolor` - Permission to use the nickcolor command to change only the nickname's color.
- `hexnicks.realname` - Permission to use the realname command.
- `hexnicks.reload` - Permission to reload the plugin.
- `hexnicks.chat.advanced` - Permission to inject things like click events into chat messages via MiniMessage.

## Colors

### Introducing gradients!

Unless changed in the config, legacy color codes (&c, &l, etc.) are not supported. They can be losely supported when the config option is enabled, but the plugin uses [MiniMessage](https://docs.adventure.kyori.net/minimessage.html) for colors and formatting.

<img align="middle" src="https://i.imgur.com/zdn80Qe.png">

Now HexNicks makes it easy to get beautiful gradients in your nicknames. The formatting is simple, for the example above the command was: `/nick <gradient:#1eae98:#d8b5ff>Majekdor</gradient>`

> Note: The closing tag is optional if you don't want anything after the initial text.

The first gradient tag can also take more than 2 hex codes, though they must be in the standard six-character hex format.


### Hex Colors:

I'll just leave this here for you :)

<img align="middle" src="https://i.pinimg.com/originals/f2/08/30/f2083044743edea046c2bc16b082b4fe.gif" height="900" width="800">

## Developers

HexNicks does have an api and all commands trigger an event when executed. These events can be listened to the same way as other Bukkit events. You can see the events [here](https://jd.hexnicks.majek.dev/dev/majek/hexnicks/api/package-summary.html) and all JavaDocs [here](https://jd.hexnicks.majek.dev/).

Event example:
```java
@EventHandler
public void onNickname(SetNickEvent event) {
  Player player = event.player();
  player.sendMessage("Setting nickname...");
  event.newNick(Component.text("New nickname"));
}
```

There are multiple ways to retrieve nicknames, but the easiest way is:
```java
Nicks.api().getNick(player); // You can pass thru a player, offlineplayer, or uuid
```

## Support

If you need help with the plugin and can't find the answer here, then the best way to get help is to either join my [Discord](https://discord.gg/CGgvDUz) or post in [discussions](https://github.com/MajekDev/HexNicks/discussions/categories/q-a). If you join Discord make sure you read the frequently-asked channel before posting in the bug-reports channel (if it's a bug) or in the hexnicks channel (for general help). 

If you have discovered a bug you can either join my [Discord](https://discord.gg/CGgvDUz) to tell me about it and then open an issue here on GitHub. An open issue is the quickest way to get me to fix it. Please do not message me on Spigot in regard to a bug, there are easier ways to communicate.


## Contributing

HexNicks is open-source and licensed under the [MIT License](https://github.com/MajekDev/HexNicks/blob/main/LICENSE), so if you want to use any code contained in the plugin or clone the repository and make some changes, go ahead!

If you've found a bug within the plugin and would like to just make the changes to fix it yourself, you're free to do so and make a pull request here on GitHub. If you make significant contributions to the project, and by significant I mean one PR to fix a typo doesn't count as significant, you can earn the Contributor role in my [Discord](https://discord.gg/CGgvDUz).


## Donate

I'm a full time college student who makes and supports these plugins in my free time (when I have any). As a long time supporter of open source, most of my plugins are free. If you enjoy my plugins and would like to support me, you can buy me coffee over on  [PayPal](https://paypal.com/paypalme/majekdor). Donations of any amount are appreciated and a donation of $10 or more will get you the Supporter role in my [Discord](https://discord.gg/CGgvDUz)!
