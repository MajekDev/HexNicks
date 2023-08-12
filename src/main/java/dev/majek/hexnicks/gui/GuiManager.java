package dev.majek.hexnicks.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

/**
 * A manager for Guis, this handles events such as {@link InventoryClickEvent} and determines which {@link Gui} to send the event to
 */
public class GuiManager implements Listener {

  private final HashMap<UUID, Gui> openGuis;

  /**
   * Create the new GUI Manager
   */
  public GuiManager() {
    this.openGuis = new HashMap<>();
  }

  /**
   * Add a GUI for this manager to handle
   *
   * @param uuid the Player who opened this gui
   * @param gui  the gui which was opened
   */
  public void registerOpenGui(@NotNull UUID uuid, @NotNull Gui gui) {
    this.openGuis.put(uuid, gui);
  }

  /**
   * Get a gui that is open for a player
   *
   * @param uuid the player
   * @return the gui that is open -- {@code null} if no gui is open
   */
  public @Nullable Gui getOpenGuiFor(@NotNull UUID uuid) {
    return this.openGuis.get(uuid);
  }

  /**
   * Close a specific gui for a player
   *
   * @param uuid the player
   * @param gui  the gui to close
   * @return true if the gui was open, false otherwise
   */
  public boolean closeGui(@NotNull UUID uuid, @NotNull Gui gui) {
    if (this.openGuis.remove(uuid, gui)) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null) {
        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
      }
      return true;
    }
    return false;
  }

  /**
   * Close the open gui for a player, if the player had this gui, then it is
   *
   * @param uuid the player
   * @return true if a gui was open, false otherwise
   */
  public boolean closeGui(@NotNull UUID uuid) {
    if (this.openGuis.remove(uuid) != null) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null) {
        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
      }
      return true;
    }
    return false;
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    Gui gui = this.getOpenGuiFor(event.getWhoClicked().getUniqueId());
    if (gui != null) {
      gui.onInventoryClick(event);
    }
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    this.openGuis.remove(event.getPlayer().getUniqueId());
  }
}
