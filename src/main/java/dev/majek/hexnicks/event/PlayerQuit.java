package dev.majek.hexnicks.event;

import dev.majek.hexnicks.HexNicks;
import dev.majek.hexnicks.config.Messages;
import dev.majek.hexnicks.message.MiniMessageWrapper;
import dev.majek.hexnicks.util.MiscUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(!HexNicks.config().FORMAT_QUIT) { return; }

        Player player = event.getPlayer();
        event.quitMessage(message(player, HexNicks.core().getDisplayName(player)));
    }

    private Component message(Player source, Component name)
    {
        final MiniMessageWrapper miniMessageWrapper = MiniMessageWrapper.builder()
                .advancedTransformations(source.hasPermission("hexnicks.chat.advanced"))
                .gradients(source.hasPermission("hexnicks.color.gradient"))
                .hexColors(source.hasPermission("hexnicks.color.hex"))
                .legacyColors(HexNicks.config().LEGACY_COLORS)
                .removeTextDecorations(MiscUtils.blockedDecorations(source))
                .removeColors(MiscUtils.blockedColors(source))
                .build();

        Component text = miniMessageWrapper.mmParse(HexNicks.hooks().applyPlaceHolders(source, HexNicks.config().QUIT_FORMAT))
                // Replace display name placeholder with HexNicks nick
                .replaceText(TextReplacementConfig.builder().matchLiteral("{displayname}")
                        .replacement(name).build()
                )
                // Replace prefix placeholder with Vault prefix
                .replaceText(TextReplacementConfig.builder().matchLiteral("{prefix}")
                        .replacement(HexNicks.hooks().vaultPrefix(source)).build()
                )
                // Replace suffix placeholder with Vault Suffix
                .replaceText(TextReplacementConfig.builder().matchLiteral("{suffix}")
                        .replacement(HexNicks.hooks().vaultSuffix(source)).build()
                );
        return text;
    }
}
