package dev.majek.hexnicks;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandNick implements CommandExecutor {

    public static Map<UUID, String> nicks = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (cmd.getName().equalsIgnoreCase("nick"))  {

            // Sender isn't a player
            if (!(sender instanceof Player)) {
                sender.sendMessage("You must be in-game to use this command."); return true;
            }

            Player player = (Player) sender;
            FileConfiguration config = HexNicks.instance.getConfig();
            int max = config.getInt("max-length");
            int min = config.getInt("min-length");
            String foo = "&#336633$This is a &btest message with a <gradient:#00a5e3:#8dd7bf>gradient</gradient> &fwoo! &c and maybe some fancy ${hover,&ahover,WOOO} text? ${";

            //player.sendMessage(TextUtils.parseExpression(BaseComponent.toLegacyText(BungeeComponentSerializer.get().serialize(MiniMessage.get().parse(HexNicks.applyColorCodes(foo))))));
            //player.sendMessage(MiniMessage.get().parse("&#336633This is a &btest message with a <gradient:#00a5e3:#8dd7bf>gradient</gradient> &fwoo!"));
            //player.sendMessage(HexNicks.format("&#336633This is a &btest message with a <gradient:#00a5e3:#8dd7bf>gradient</gradient> &fwoo!"));

            if (config.getBoolean("use-permissions", false)) {
                if (!player.hasPermission("hexnicks.use")) {
                    player.sendMessage(TextUtils.applyColorCodes(config.getString("no-permission")));
                    return true;
                }
            }

            if (args.length >= 1) {
                String nick, noColor;
                if (args[0].equalsIgnoreCase("help")) {
                    for (String string : config.getStringList("help-messages"))
                        player.sendMessage(TextUtils.applyColorCodes(string.replace("%max%", String.valueOf(max))));
                    return true;
                }

                if (args.length > 1) {
                    if (player.hasPermission("hexnicks.admin")) {
                        Player target = Bukkit.getPlayer(args[0]);
                        if (target == null) {
                            nick = getNickFromMsg(args, player);
                            if (nick.equals("%ERROR%"))
                                return true;
                            setNickname(player, nick, true);
                            return true;
                        }
                        args = (String[]) ArrayUtils.remove(args, 0);
                        nick = getNickFromMsg(args, player);
                        if (nick.equals("%ERROR%"))
                            return true;
                        setNickname(target, nick, true);
                        player.sendMessage(TextUtils.applyColorCodes((config.getString("other-nickname-set") + "")
                                .replace("%nick%", nick)));
                        return true;
                    }
                    nick = getNickFromMsg(args, player);
                    if (nick.equals("%ERROR%"))
                        return true;
                    setNickname(player, nick, true);
                } else
                    nick = args[0];

                noColor = getNickNoFormat(nick);
                if (noColor.length() > max) {
                    player.sendMessage(TextUtils.applyColorCodes(config.getString("name-too-long"))); return true;
                }
                if (noColor.length() < min) {
                    player.sendMessage(TextUtils.applyColorCodes(config.getString("name-too-short"))); return true;
                }

                setNickname(player, nick, true);
                return true;
            } else
               return false;
        }

        if (cmd.getName().equalsIgnoreCase("nonick")) {

            // Sender isn't a player
            if (!(sender instanceof Player)) {
                sender.sendMessage("You must be in-game to use this command.");
                return true;
            }

            Player player = (Player) sender;
            FileConfiguration config = HexNicks.instance.getConfig();

            if (config.getBoolean("use-permissions")) {
                if (!player.hasPermission("hexnicks.use")) {
                    player.sendMessage(TextUtils.applyColorCodes(config.getString("no-permission")));
                    return true;
                }
            }

            if (args.length > 0) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(TextUtils.applyColorCodes(config.getString("player-not-found")));
                    return true;
                }

                setNickname(target, target.getName(), false);

                player.sendMessage(TextUtils.applyColorCodes(config.getString("nickname-removed")));
                target.sendMessage(TextUtils.applyColorCodes(config.getString("nickname-removed")));
                return true;
            } else  {

                setNickname(player, player.getName(), false);

                player.sendMessage(TextUtils.applyColorCodes(config.getString("nickname-removed")));
            }

            return true;
        }

        if (cmd.getName().equalsIgnoreCase("nickcolor")) {

            // Sender isn't a player
            if (!(sender instanceof Player)) {
                sender.sendMessage("You must be in-game to use this command.");
                return true;
            }

            Player player = (Player) sender;
            FileConfiguration config = HexNicks.instance.getConfig();

            if (config.getBoolean("use-permissions")) {
                if (!player.hasPermission("hexnicks.changecolor")) {
                    player.sendMessage(TextUtils.applyColorCodes(config.getString("no-permission")));
                    return true;
                }
            }
            if (args.length == 1) {
                String nick;
                if (TextUtils.removeColorCodes(args[0]).equals("")) {
                    nick = args[0] + player.getName();
                } else if (TextUtils.removeColorCodes(args[0]).equalsIgnoreCase(player.getName())) {
                    nick = args[0];
                } else {
                    player.sendMessage(TextUtils.applyColorCodes(config.getString("only-color-codes")));
                    return true;
                }

                setNickname(player, nick, true);

                return true;
            } else
                return false;
        }

        if (cmd.getName().equalsIgnoreCase("hexreload")) {
            FileConfiguration config = HexNicks.instance.getConfig();
            if (sender.hasPermission("hexnicks.admin")) {
                HexNicks.instance.reloadConfig();
                HexNicks.instance.loadNicksFromJSON();
                sender.sendMessage(TextUtils.applyColorCodes(config.getString("config-reloaded")));
            } else {
                sender.sendMessage(TextUtils.applyColorCodes(config.getString("no-permission")));
            }
            return true;
        }
        return false;
    }

    public static String getNickNoFormat(String nick) {
        // Separate lines to help with debugging if something goes wrong
        String finalNick = nick;
        Component nickComp = MiniMessage.get().parse(finalNick);
        BaseComponent[] nickBase = BungeeComponentSerializer.get().serialize(nickComp);
        finalNick = BaseComponent.toLegacyText(nickBase);
        return TextUtils.removeColorCodes(finalNick);
    }

    public static void setNickname(Player player, String nick, boolean sendMessage) {
        FileConfiguration config = HexNicks.instance.getConfig();

        // Separate lines to help with debugging if something goes wrong
        String finalNick = nick;
        if (player.hasPermission("hexnicks.colors.hex")) {
            Component nickComp = MiniMessage.get().parse(finalNick);
            BaseComponent[] nickBase = BungeeComponentSerializer.get().serialize(nickComp);
            finalNick = BaseComponent.toLegacyText(nickBase);
            finalNick = TextUtils.applyColorCodes(finalNick + "&r&f");
        } else if (player.hasPermission("hexnicks.colors.normal"))
            finalNick = TextUtils.applyColorCodes(finalNick, false, true);
        else
            finalNick = TextUtils.removeColorCodes(finalNick);

        player.setDisplayName(finalNick);

        if (config.getBoolean("tab-nicknames"))
            player.setPlayerListName(finalNick);

        nicks.put(player.getUniqueId(), finalNick);

        try {
            HexNicks.instance.jsonConfig.putInJSONObject(player.getUniqueId().toString(), finalNick);
        } catch (IOException | ParseException e) {
            HexNicks.instance.getLogger().severe("Error saving nickname to nicknames.json data file.");
            e.printStackTrace();
        }

        // SQL shit
        if (HexNicks.instance.SQL.isConnected())
            HexNicks.instance.data.addNickname(player.getUniqueId(), finalNick);
        if (sendMessage)
            player.sendMessage(TextUtils.applyColorCodes((config.getString("nickname-set") + "").replace("%nick%", finalNick)));
    }

    public String getNickFromMsg(String[] args, Player player) {
        FileConfiguration config = HexNicks.instance.getConfig();
        String arguments = String.join(" ", args);

        String nick;
        int count = 0;
        for (char c : arguments.toCharArray())
            if (c == '\"')
                count++;
        if (count == 2) {
            arguments = arguments.replaceFirst("\"", "(");
            arguments = replaceLast(arguments, "\"", ")");
            nick = getEnclosed(0, arguments).getFirst();
        } else if (count == 0)
            nick = args[0];
        else {
            for (String string : config.getStringList("spaces-prompt"))
                player.sendMessage(TextUtils.applyColorCodes(string));
            return "%ERROR%";
        }
        return nick;
    }

    public static Pair<String, Integer> getEnclosed(int start, String string) {
        boolean curved = string.charAt(start) == '(';
        int depth = 1, i = start + 1;
        while(depth > 0) {
            if(i == string.length())
                return new Pair<>(null, -1);
            char c = string.charAt(i++);
            if(c == (curved ? ')' : '}'))
                -- depth;
            else if(c == (curved ? '(' : '{'))
                ++ depth;
        }
        return new Pair<>(string.substring(start + 1, i - 1), i);
    }

    public String replaceLast(String string, String substring, String replacement) {
        int index = string.lastIndexOf(substring);
        if (index == -1)
            return string;
        return string.substring(0, index) + replacement
                + string.substring(index+substring.length());
    }
}
