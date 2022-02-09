---
id: color-formatting
title: Color Formatting
slug: /color-formatting
---

One of the most important parts of HexNicks is, of course, colors! With the addition of [MiniMessage](https://github.com/KyoriPowered/adventure-text-minimessage) there are now even more ways to customize colors in your nickname. HexNicks can support legacy colors but supports, and encourages, MiniMessage colors.

## MiniMessage Colors
MiniMessage uses tags for colors and formatting. Tags have a start tag and an end tag. Start tags are mandatory (obviously), end tags aren’t. `<yellow>Hello <blue>World<yellow>!` and `<yellow>Hello <blue>World</blue>!` and even `<yellow>Hello </yellow><blue>World</blue><yellow>!</yellow>` all do the same. Read the full documentation for MiniMessage [here](https://docs.adventure.kyori.net/minimessage#format).

## Legacy Colors
These are the color codes that a lot of people are still used to. Codes like `&a`, `&l`, `&x&r&r&g&g&b&b`. These are called legacy codes because the Minecraft chat, and other things, does not use raw strings anymore. By default, HexNicks will not parse these codes. If you want to use them in your nicknames you need to enable `legacy-support` in the config.

> See config documentation [here](https://github.com/MajekDev/HexNicks/wiki/Configuration-Options).

If enabled these codes will be parsed and probably work fine, though you will not receive support for issues relating to legacy codes.

## Gradients
Everyone's favorite thing as of late, gradients! Gradients are parsed using MiniMessage, and you can read the documentation for them [here](https://docs.adventure.kyori.net/minimessage#gradient).

> View nickname examples using gradients here.