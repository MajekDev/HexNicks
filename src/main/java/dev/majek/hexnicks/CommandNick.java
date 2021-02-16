package dev.majek.hexnicks;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

            if (config.getBoolean("use-permissions", false)) {
                if (!player.hasPermission("hexnicks.use")) {
                    player.sendMessage(HexNicks.colorize(config.getString("no-permission")));
                    return true;
                }
            }

            if (args.length >= 1) {
                String nick, noColor;
                if (args[0].equalsIgnoreCase("help")) {
                    for (String string : config.getStringList("help-messages"))
                        player.sendMessage(HexNicks.colorize(string.replace("%max%", String.valueOf(max))));
                    return true;
                }

                if (args.length > 1) {
                    if (player.hasPermission("hexnicks.admin")) {
                        Player target = Bukkit.getPlayer(args[0]);
                        if (target == null) {
                            nick = getNick(args, player);
                            if (nick.equals("%ERROR%"))
                                return true;
                            setNick(player, nick, true);
                            return true;
                        }
                        args = (String[]) ArrayUtils.remove(args, 0);
                        nick = getNick(args, player);
                        if (nick.equals("%ERROR%"))
                            return true;
                        setNick(target, nick, true);
                        player.sendMessage(HexNicks.colorize((config.getString("other-nickname-set") + "")
                                .replace("%nick%", nick)));
                        return true;
                    }
                    nick = getNick(args, player);
                    if (nick.equals("%ERROR%"))
                        return true;
                    setNick(player, nick, true);
                } else
                    nick = args[0];

                noColor = HexNicks.removeColorCodes(nick);
                if (noColor.length() > max) {
                    player.sendMessage(HexNicks.colorize(config.getString("name-too-long"))); return true;
                }
                if (noColor.length() < min) {
                    player.sendMessage(HexNicks.colorize(config.getString("name-too-short"))); return true;
                }
                
                setNick(player, nick, true);
                return true;
            } else
               return false;
        }
        if (cmd.getName().equalsIgnoreCase("nonick")) {

            // Sender isn't a player
            if (!(sender instanceof Player)) {
                sender.sendMessage("You must be in-game to use this command."); return true;
            }

            Player player = (Player) sender; FileConfiguration config = HexNicks.instance.getConfig();

            if (config.getBoolean("use-permissions")) {
                if (!player.hasPermission("hexnicks.use")) {
                    player.sendMessage(HexNicks.colorize(config.getString("no-permission")));
                    return true;
                }
            }

            if (player.hasPermission("hexnicks")) {
                if (args.length > 0) {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target == null) {
                        player.sendMessage(HexNicks.colorize(config.getString("player-not-found")));
                        return true;
                    }
                    setNick(target, target.getName(), false);
                    player.sendMessage(HexNicks.colorize(config.getString("nickname-removed")));
                    target.sendMessage(HexNicks.colorize(config.getString("nickname-removed")));
                    return true;
                } else  {
                    setNick(player, player.getName(), false);
                    player.sendMessage(HexNicks.colorize(config.getString("nickname-removed")));
                }
            } else {
                setNick(player, player.getName(), false);
                player.sendMessage(HexNicks.colorize(config.getString("nickname-removed")));
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("nickcolor")) {

            // Sender isn't a player
            if (!(sender instanceof Player)) {
                sender.sendMessage("You must be in-game to use this command."); return true;
            }

            Player player = (Player) sender; FileConfiguration config = HexNicks.instance.getConfig();

            if (config.getBoolean("use-permissions")) {
                if (!player.hasPermission("hexnicks.changecolor")) {
                    player.sendMessage(HexNicks.colorize(config.getString("no-permission")));
                    return true;
                }
            }
            if (args.length == 1) {
                String nick;
                if (HexNicks.removeColorCodes(args[0]).equals("")) {
                    nick = args[0] + player.getName();
                } else if (HexNicks.removeColorCodes(args[0]).equalsIgnoreCase(player.getName())) {
                    nick = args[0];
                } else {
                    player.sendMessage(HexNicks.colorize(config.getString("only-color-codes")));
                    return true;
                }
                setNick(player, nick, true);
                return true;
            } else
                return false;
        }
        if (cmd.getName().equalsIgnoreCase("hexreload")) {
            FileConfiguration config = HexNicks.instance.getConfig();
            if (sender.hasPermission("hexnicks.admin")) {
                HexNicks.instance.reloadConfig();
                sender.sendMessage(HexNicks.colorize(config.getString("config-reloaded"))); return true;
            } else {
                sender.sendMessage(HexNicks.colorize(config.getString("no-permission"))); return true;
            }
        }
        return false;
    }

    public void setNick(Player player, String nick, boolean sendMessage) {
        FileConfiguration config = HexNicks.instance.getConfig();
        player.setDisplayName(HexNicks.colorize(nick + "&r"));

        if (config.getBoolean("tab-nicknames"))
            player.setPlayerListName(HexNicks.colorize(nick));

        nicks.put(player.getUniqueId(), nick);

        // SQL shit
        if (HexNicks.instance.SQL.isConnected())
            HexNicks.instance.data.addNickname(player.getUniqueId(), nick);
        if (sendMessage)
            player.sendMessage(HexNicks.colorize((config.getString("nickname-set") + "").replace("%nick%", nick)));
    }

    public String getNick(String[] args, Player player) {
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
                player.sendMessage(HexNicks.colorize(string));
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
