---
id: config-options
title: Configuration Options
slug: /config-options
---

HexNicks' [config](https://github.com/MajekDev/HexNicks/blob/main/src/main/resources/config.yml) is documented fairly 
well but this page will try to explain some of the more confusing bits.

## Require Alphanumeric

This config option requires all nicknames to contain nothing but characters a-z, lower and upper case, 
and numbers. That does not include color codes, but the raw nickname you see in chat ignoring colors must 
only contain alphanumeric characters.

## Legacy Colors

By default, legacy colors like `&a` and `&6` **will not work**. This is because they are **legacy**. However, 
because so many people like to use them, HexNicks does support them with this config option enabled.

## Update Prompt

When this is enabled and the server isn't running the latest version of HexNicks, the plugin will send a message 
to any player who is an operator when they join saying that the plugin has an update available.

## Override Essentials

Compatibility with [Essentials](https://github.com/EssentialsX/Essentials) has plagued HexNicks since it was 
released. Essentials just tries to do so many things it frequently causes conflicts. If this config options is 
enabled and Essentials is detected, HexNicks will set the player's Essentials nickname as well as their display 
name on nickname creation. This is just meant to prevent conflicts.

If HexNicks is doing something like setting tab names to nicknames even though that setting 
is disabled, you probably need to disable this.

## `/nickother` Override

When enabled, this allows players who have the ability to use `/nickother` to override the target player's permissions for formatting codes.

This means you can now give someone a nickname using the color aqua even if they don't have permission to use that color.

If the config option is disabled then `/nickother` will obey the target player's permissions for formatting.