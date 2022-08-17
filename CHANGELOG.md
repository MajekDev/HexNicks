# Plugin Changelog

# 3.0.4 - Logging Is A Feature

- Added a lot of logging when in debug mode.

This logging was meant to help find an issue... then the issue fixed itself. The logging could still be helpful in finding future issues, though.

# 3.0.3 - Small Patches

- Fixed bug with messages on `/nickother`.
- Messages will only send provided they're not empty.
- Added `package-info.java` to message package.

Another very minor update with some useful fixes.

# 3.0.2 - Async Improvements

- Nickname commands (`/nick`, `/nickcolor`, and `/nickother`) will now run asynchronously to prevent a server slowdown and a "Working..." message will be displayed until the command completes.
- Placeholders should resolve faster and prevent server slowdown.
- Patched critical bug allow advanced transformations to be injected in chat messages.

A small update that should prevent HexNicks from slowing down your server in various ways.

# 3.0.1 - Small Patches

- Fixed bug with `nicknames.json` failing to generate.
- Bumped dependencies.

Another very minor update with some useful fixes.

# 3.0.0 - P.S. (Post Spigot)

- Dropped support for SpigotMC. HexNicks will only work on Paper servers 1.18.2 and above.
- Added the ability to edit the config file via a pastebin.
- Implemented logging to make it easier to diagnose issues.
- Switched text decoration blocking from a config option to a permission node.
- Removed deprecated methods.

This update was a major overhaul of the entire plugin. You can read more about the update [here](https://hexnicks.majek.dev/v3). Dropping Spigot is a big leap, but Paper is the better ecosystem for the future and supporting both was becoming impossible.

# 2.2.2 - Small Patches

- A few bug fixes related to color parsing.
- Your own nickname/username won't count against you when preventing duplicate nicknames.
- Other player usernames will also be prevented from being used in /nick.

Another very minor update with some useful fixes.

# 2.2.1 - No Duplicate Nicknames

- New config option for preventing players from having the same nickname.
- Added ability to block color usage with permissions, read [here](https://github.com/MajekDev/HexNicks/wiki/Permissions#blocking-certain-color-usage).
- PlaceholderAPI placeholders will now be parsed in chat format.
- Chat format can now contain legacy codes on Paper if legacy support is enabled.
- Fix typo permission issue.
- Standard code cleanup.

This is a very minor update that adds a lot of small new features and squashes some bugs.

# 2.2.0 - Async Everything

- Switched access to MySQL database from synchronous to asynchronous.
- Added support for `autoReconnect` flag for MySQL.
- Implemented feature requested in #36. Options for disabling text decorations.
- Did some normal code cleanup.
- Fixed bug with overflowing colors in chat on Spigot.

Okay not async *everything* but a lot of stuff that should be async now is!

# 2.1.4 - Essentials Compatibility

- New EssentialsHook should allow HexNicks and Essentials to work with one another out of the box.
- Config option for whether to override Essentials nick with new hook (true by default).
- Add JavaDocs and sources on Maven deploy.
- Fixed UpdateChecker bug with -SNAPSHOT versions.
- Add JavaDocs to main branch in /docs.
- Refactor logger from SL4J to Bukkit standard.

Essentials compatibility has been plaguing HexNicks since the beginning. It's by far the most talked about issue in 
my Discord. This update should allow the two plugins to work together out of the box.