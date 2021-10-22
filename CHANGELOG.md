# Plugin Changelog

# 2.1.4 - Essentials Compatibility

- New EssentialsHook should allow HexNicks and Essentials to work with one another out of the box.
- Config option for whether to override Essentials nick with new hook (true by default).
- Add JavaDocs and sources on Maven deploy.
- Fixed UpdateChecker bug with -SNAPSHOT versions.
- Add JavaDocs to main branch in /docs.
- Refactor logger from SL4J to Bukkit standard.

Essentials compatibility has been plaguing HexNicks since the beginning. It's by far the most talked about issue in 
my Discord. This update should allow the two plugins to work together out of the box.