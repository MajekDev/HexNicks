package dev.majek.hexnicks.event;

import dev.majek.hexnicks.HexNicks;
import dev.majek.hexnicks.message.MiniMessageWrapper;
import dev.majek.hexnicks.util.MiscUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class PlayerChat implements Listener {

  /**
   * Fires on chat to format. Lowest priority to allow other plugins to modify over us.
   *
   * @param event AsyncChatEvent.
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void onChat(final AsyncChatEvent event) {
    if (HexNicks.config().CHAT_FORMATTER) {
      HexNicks.logging().debug("paper chat event fired");
      event.renderer((source, sourceDisplayName, message, viewer) ->
          formatChat(source, PlainTextComponentSerializer.plainText().serialize(message))
      );
    }
  }

  /**
   * Format the chat for all server implementations.
   *
   * @param source the chatter
   * @param message the message
   * @return formatted chat
   */
  private @NotNull Component formatChat(final @NotNull Player source, final @NotNull String message) {
    final MiniMessageWrapper miniMessageWrapper = MiniMessageWrapper.builder()
        .gradients(source.hasPermission("hexnicks.color.gradient"))
        .hexColors(source.hasPermission("hexnicks.color.hex"))
        .legacyColors(HexNicks.config().LEGACY_COLORS)
        .removeTextDecorations(MiscUtils.blockedDecorations(source))
        .removeColors(MiscUtils.blockedColors(source))
        .build();

    return miniMessageWrapper.mmParse(HexNicks.hooks().applyPlaceHolders(source, HexNicks.config().CHAT_FORMAT))
        // Replace display name placeholder with HexNicks nick
        .replaceText(TextReplacementConfig.builder().matchLiteral("{displayname}")
            .replacement(HexNicks.core().getDisplayName(source)).build()
        )
        // Replace prefix placeholder with Vault prefix
        .replaceText(TextReplacementConfig.builder().matchLiteral("{prefix}")
            .replacement(HexNicks.hooks().vaultPrefix(source)).build()
        )
        // Replace suffix placeholder with Vault Suffix
        .replaceText(TextReplacementConfig.builder().matchLiteral("{suffix}")
            .replacement(HexNicks.hooks().vaultSuffix(source)).build()
        )
        // Replace message placeholder with the formatted message from the event
        .replaceText(TextReplacementConfig.builder().matchLiteral("{message}")
            .replacement(miniMessageWrapper.mmParse(message)).build()
        );
  }
}
