package dev.majek.hexnicks;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPI extends PlaceholderExpansion {

    private final HexNicks plugin;
    private String yes;
    private String no;

    public PlaceholderAPI(HexNicks plugin){
        this.plugin = plugin;
        try {
            yes = PlaceholderAPIPlugin.booleanTrue();
            no = PlaceholderAPIPlugin.booleanFalse();
        } catch (Exception err) {
            plugin.getLogger().info("Unable to hook into PAPI API for boolean results. Defaulting...");
        }
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public @NotNull String getAuthor(){
        return plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getIdentifier(){
        return plugin.getDescription().getName().toLowerCase();
    }

    @Override
    public @NotNull String getVersion(){
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {

        if (identifier.equalsIgnoreCase("nick"))
            return CommandNick.nicks.get(player.getUniqueId()) == null ? player.getName()
                    : TextUtils.applyColorCodes(CommandNick.nicks.get(player.getUniqueId()));

        return null;
    }

}

