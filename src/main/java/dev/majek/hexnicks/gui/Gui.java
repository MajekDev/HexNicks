package dev.majek.hexnicks.gui;

import dev.majek.hexnicks.HexNicks;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

public abstract class Gui {

  private final Inventory inventory;
  private Player user;

  // Map of slot : slot action -- slot action can either be Consumer<InventoryAction> or Runnable
  private final HashMap<Integer, Object> actionMap;

  // The item to use as a background colour
  private static final ItemStack BLANK_ITEM;

  static {
    BLANK_ITEM = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    ItemMeta meta = BLANK_ITEM.getItemMeta();
    meta.displayName(Component.empty());
    BLANK_ITEM.setItemMeta(meta);
  }

  /**
   * Create a gui with a given size and a name
   *
   * @param size The amount of slots for the GUI -- must be a multiple of 9
   * @param name The name for the gui, shown in the top-left corner
   */
  public Gui(@Range(from = 1, to = 54) int size, @NotNull Component name) {
    this.inventory = Bukkit.createInventory(null, size, name);
    this.user = null;
    this.actionMap = new HashMap<>();
  }

  /**
   * Open the gui for a player, this will register the gui in the GuiManager to handle all events, and intialise the inventory
   *
   * @param player the player to open this gui for
   */
  public void openGui(Player player) {
    this.user = player;
    fillInventory();
    player.openInventory(inventory);
    HexNicks.guis().registerOpenGui(this.user.getUniqueId(), this);
  }

  /**
   * Set the contents of the inventory to only {@link Gui#BLANK_ITEM}.
   *
   * <i>Note: This overwrites all slots in the inventory and should be used before setting other items.</i>
   */
  protected void blankInventory() {
    ItemStack[] contents = new ItemStack[this.inventory.getSize()];
    // Clone here so that the user may modify the inventory slots without changing it for _all_ guis
    Arrays.fill(contents, BLANK_ITEM.clone());
    this.inventory.setContents(contents);
  }

  /**
   * Add a button into the gui that may run an action
   *
   * @param slot   the slot in which to place the item stack
   * @param stack  the stack to be placed -- This is _not_ cloned
   * @param action the action to be run
   */
  protected void addActionButton(int slot, ItemStack stack, Runnable action) {
    this.inventory.setItem(slot, stack);
    this.actionMap.put(slot, action);
  }

  /**
   * Add a button into the gui that may run an action
   *
   * @param slot   the slot in which to place the item stack
   * @param stack  the stack to be placed -- This is _not_ cloned
   * @param action the action to be run, passed the inventory action that occurs on the click.
   */
  protected void addActionButton(int slot, ItemStack stack, Consumer<InventoryAction> action) {
    this.inventory.setItem(slot, stack);
    this.actionMap.put(slot, action);
  }

  /**
   * Fill the inventory with items.  This is run when the inventory is first opened for a player and may be run after to refresh the gui.
   * <p>
   * It is recommended to call {@link Gui#blankInventory()} at the beginning if you want a background.
   */
  protected abstract void fillInventory();

  /**
   * Handle the inventory click event for this gui
   *
   * @param event the event that occurred
   */
  public void onInventoryClick(InventoryClickEvent event) {
    if (this.inventory().equals(event.getClickedInventory())) {
      this.onInventoryClick(event.getSlot(), event.getAction());
      event.setCancelled(true);
    }
  }

  /**
   * Handle inventory click on a specific slot for this gui
   * <p>
   * Default implementation is to call the action for that slot
   *
   * @param slot      the slot which has been clicked
   * @param invAction the InventoryAction that was called on the slot
   */
  protected void onInventoryClick(int slot, InventoryAction invAction) {
    Object action = this.actionMap.get(slot);
    if (action != null) {
      if (action instanceof Runnable runnable) {
        runnable.run();
      } else if (action instanceof Consumer) {
        // This cast is okay because we know that the only way to insert a Consumer into the map is to use `addActionButton(.., Consumer<InventoryAction>)`.
        ((Consumer<InventoryAction>) action).accept(invAction);
      }
    }
  }

  protected Inventory inventory() {
    return this.inventory;
  }

  protected Player user() {
    return this.user;
  }
}
