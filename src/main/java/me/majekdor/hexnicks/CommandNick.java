package me.majekdor.hexnicks;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandNick implements CommandExecutor {

    public static Map<String, String> nicks = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (cmd.getName().equalsIgnoreCase("nick"))  {

            // Sender isn't a player
            if (!(sender instanceof Player)) {
                sender.sendMessage(HexNicks.format("&cSorry console, no nickname for you."));
                return true;
            }

            Player p = (Player) sender; FileConfiguration c = HexNicks.instance.getConfig();
            int max = c.getInt("max-length");
            int min = c.getInt("min-length");

            if (c.getBoolean("use-permissions")) {
                if (!p.hasPermission("hexnicks.use")) {
                    p.sendMessage(HexNicks.format("&cYou don't have permission to change your nickname!"));
                    return true;
                }
            }

            if (args.length >= 1) {
                String nick, noColor;
                if (args[0].equalsIgnoreCase("help")) {
                    p.sendMessage(HexNicks.format("&7----------&cNickname &6Help&7----------"));
                    p.sendMessage(HexNicks.format("&cUsage: &7/nick <nickname>"));
                    p.sendMessage(HexNicks.format("&7You may use hex and standard color codes."));
                    p.sendMessage(HexNicks.format("&7Use &c/nonick &7to remove your nickname."));
                    p.sendMessage(HexNicks.format("&7Max nickname length not including colors is: &c" + max));
                    return true;
                }
                // This section looks dumb as shit but trust me it works
                if (args.length > 1) {
                    char[] chars = Arrays.toString(args).toCharArray();
                    StringBuilder sb = new StringBuilder();
                    int helpful = 0;
                    for (char aChar : chars) {
                        if (helpful < 2) {
                            if (aChar == '\"') {
                                helpful++;
                                continue;
                            }
                            if (helpful == 1) {
                                sb.append(aChar);
                            }
                        }
                    }
                    if (helpful != 2) {
                        p.sendMessage(HexNicks.format("&cIt seems as if you have tried to add a space in your nickname."));
                        p.sendMessage(HexNicks.format("&cTo do this, use /nick \"part1 part2\" with quotations."));
                        return true;
                    } else {
                        nick = sb.toString().replaceAll(",", "");
                    }
                } else {
                    nick = args[0];
                }
                noColor = HexNicks.removeColorCodes(nick);
                if (noColor.length() > max) {
                    p.sendMessage(HexNicks.format("&cThis nickname is too long!")); return true;
                }
                if (noColor.length() < min) {
                    p.sendMessage(HexNicks.format("&cThis nickname is too short!")); return true;
                }
                
                p.setDisplayName(HexNicks.format(nick + "&r"));

                if (c.getBoolean("tab-nicknames")) {
                    p.setPlayerListName(HexNicks.format(nick));
                }

                if (c.getBoolean("player-head-nick")) {
                    changeName(nick, p);
                }

                if (nicks.containsKey(p.getName())) {
                    nicks.replace(p.getName(), nick);
                } else {
                    nicks.put(p.getName(), nick);
                }
                p.sendMessage(HexNicks.format("&7Your nickname is now: " + nick));
            } else {
                p.sendMessage(HexNicks.format("&cUsage: /nick <nickname> | Use /nick help for more"));
            }
        }
        if (cmd.getName().equalsIgnoreCase("nonick")) {
            // Sender isn't a player
            if (!(sender instanceof Player)) {
                sender.sendMessage(HexNicks.format("&cConsole is unable to run this command."));
                return true;
            }
            Player p = (Player) sender; FileConfiguration c = HexNicks.instance.getConfig();
            if (c.getBoolean("use-permissions")) {
                if (!p.hasPermission("hexnicks.use")) {
                    p.sendMessage(HexNicks.format("&cYou don't have permission to change your nickname!"));
                    return true;
                }
            }
            if (args.length == 0) {
                p.setDisplayName(p.getName());
                if (c.getBoolean("tab-nicknames")) {
                    p.setPlayerListName(HexNicks.format(p.getName()));
                }
                if (nicks.containsKey(p.getName())) {
                    nicks.replace(p.getName(), p.getName());
                } else {
                    nicks.put(p.getName(), p.getName());
                }
                p.sendMessage(HexNicks.format("&7Nickname removed."));
            } else {
                p.sendMessage(HexNicks.format("&cUsage: /nick <nickname> | Use /nick help for more"));
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static void changeName(String name, Player player) {
        try {
            Method getHandle = player.getClass().getMethod("getHandle");
            Object entityPlayer = getHandle.invoke(player);
            /*
             * These methods are no longer needed, as we can just access the
             * profile using handle.getProfile. Also, because we can just use
             * the method, which will not change, we don't have to do any
             * field-name look-ups.
             */
            boolean gameProfileExists = false;
            // Some 1.7 versions had the GameProfile class in a different package
            try {
                Class.forName("net.minecraft.util.com.mojang.authlib.GameProfile");
                gameProfileExists = true;
            } catch (ClassNotFoundException ignored) {

            }
            try {
                Class.forName("com.mojang.authlib.GameProfile");
                gameProfileExists = true;
            } catch (ClassNotFoundException ignored) {

            }
            if (!gameProfileExists) {
                /*
                 * Only 1.6 and lower servers will run this code.
                 *
                 * In these versions, the name wasn't stored in a GameProfile object,
                 * but as a String in the (final) name field of the EntityHuman class.
                 * Final (non-static) fields can actually be modified by using
                 * {@link java.lang.reflect.Field#setAccessible(boolean)}
                 */
                Field nameField = entityPlayer.getClass().getSuperclass().getDeclaredField("name");
                nameField.setAccessible(true);
                nameField.set(entityPlayer, HexNicks.format(name));
            } else {
                // Only 1.7+ servers will run this code
                Object profile = entityPlayer.getClass().getMethod("getProfile").invoke(entityPlayer);
                Field ff = profile.getClass().getDeclaredField("name");
                ff.setAccessible(true);
                ff.set(profile, HexNicks.format(name));
            }
            // In older versions, Bukkit.getOnlinePlayers() returned an Array instead of a Collection.
            if (Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).getReturnType() == Collection.class) {
                Collection<? extends Player> players = (Collection<? extends Player>) Bukkit.class.getMethod("getOnlinePlayers").invoke(null);
                for (Player p : players) {
                    p.hidePlayer(player);
                    p.showPlayer(player);
                }
            } else {
                Player[] players = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers").invoke(null));
                for (Player p : players) {
                    p.hidePlayer(player);
                    p.showPlayer(player);
                }
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException e) {
            /*
             * Merged all the exceptions. Less lines
             */
            e.printStackTrace();
        }
    }

}
