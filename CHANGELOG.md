# Plugin Changelog

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