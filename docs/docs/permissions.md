---
id: permissions
title: Permissions
slug: /permissions
---

All permissions except `hexnicks.nick.other`, `hexnicks.nonick.other`, `hexnicks.chat.advanced`, `hexnicks.config-editor`, and `hexnicks.reload` are given to all players by default but can be negated by a permissions manager like [LuckPerms](https://luckperms.net/). Learn how to do that [here](#how-to-negate-permissions-with-LuckPerms).

A lot of these permissions are tied to commands, read command documentation [here](https://github.com/Majekdor/HexNicks/wiki/Commands).

## hexnicks.nick

This permission node allows players to use the command [/nick](https://github.com/Majekdor/HexNicks/wiki/Commands#nick) to change their nickname.

## hexnicks.nick.other

This permission node is intended for staff and allows them to use the command [/nickother](https://github.com/Majekdor/HexNicks/wiki/Commands#nickother) to change the nickname of other players.

## hexnicks.nonick

This permission node allows players to use the command [/nonick](https://github.com/Majekdor/HexNicks/wiki/Commands#nonick) to remove their nickname.

## hexnicks.nonick.other

This permission node is intended for staff and allows them to remove the nickname of another player using [/nonick](https://github.com/Majekdor/HexNicks/wiki/Commands#nonick).

## hexnicks.nickcolor

This permission node allows players to use [/nickcolor](https://github.com/Majekdor/HexNicks/wiki/Commands#nickcolor) to change just the color of their nickname.

## hexnicks.realname

This permission node allows players to use [/realname](https://github.com/Majekdor/HexNicks/wiki/Commands#realname) to view the name of a player using a certain nickname.

## hexnicks.config-editor

This permission node is intended for staff and allows them to edit the HexNicks config via [pastebin](https://paste.majek.dev) using [/hexnicks config-editor](https://github.com/Majekdor/HexNicks/wiki/Commands#hexnicks).

## hexnicks.reload

This permission node is intended for staff and allows them to use [/hexnicks reload](https://github.com/Majekdor/HexNicks/wiki/Commands#hexnicks) to reload the plugin.

## hexnicks.chat.advanced

This permission node is a dangerous one to grant as it allows players to inject things like hover events and click-to-run-command events into chat. Players without this permission will only be able to use MiniMessage tags for colors in chat. This permission allows them to use all MiniMessage tags in chat.

> Read about chat formatting [here](https://hexnicks.majek.dev/chat-formatting).

## hexnicks.color.*

This permission node allows players to use all standard color codes (ex. \<red\>, \<dark_blue\>) in their nicknames. You can specify only specific colors with `hexnicks.color.red` (or any of the other 16 Mincraft color names).

> Read about color formatting [here](https://hexnicks.majek.dev/color-formatting).

## hexnicks.color.hex

This permission node allows players to use hex color codes (ex. \<#aabbcc\>, \<color:#aabbcc\>) in their nicknames.

## hexnicks.color.gradients

This permission node allows players to use gradients in their nicknames.

> See gradient examples [here](https://hexnicks.majek.dev/color-formatting#gradients).

## Blocking Certain Color Usage

By default, all players have permission to use all colors. You can view all of the permissions [here](https://github.com/MajekDev/HexNicks/blob/main/src/main/resources/plugin.yml#L60). To prevent a player from using a certain color code simply negate the permission for the color you want to block.

## How to negate permissions with LuckPerms

If you wanted to, for example, only give certain groups permission to use gradients, you would need to negate the permission for the other groups. To do this using LuckPerms, you would run
```
/lp group <group> permission set hexnicks.nick.gradient false
```
replacing `<group>` with the name of the group you're modifying.
