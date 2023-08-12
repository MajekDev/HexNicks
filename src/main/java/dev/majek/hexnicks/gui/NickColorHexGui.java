package dev.majek.hexnicks.gui;

import dev.majek.hexnicks.HexNicks;
import dev.majek.hexnicks.message.MiniMessageWrapper;
import dev.majek.hexnicks.util.CustomHead;
import dev.majek.hexnicks.util.MiscUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Random;

/**
 * Gui for choosing a hexadecimal colour to use for nickname
 */
public class NickColorHexGui extends Gui {

  // The steps to be used for each button
  private static final int[] STEPS = {16, 8, 1, -1, -8, -16};

  // Arrays that hold the r/g/b color buttons
  private static final ItemStack[] RED;
  private static final ItemStack[] GREEN;
  private static final ItemStack[] BLUE;

  // Rainbow button for random color
  private static final ItemStack RAINBOW;
  // Back button for back to simple color codes
  private static final ItemStack BACK_BUTTON;

  private static final Random RNG = new Random();

  private int red, green, blue;
  private String nickname;

  /**
   * Generate the array of items that should be used for changing the r/g/b values
   *
   * @param name  the name of the value that should be displayed (i.e. "Red")
   * @param color The colour of the text to be displayed
   * @param base  The base {@link ItemStack} that should be used (this is cloned for each item in the array)
   * @return An array with 6 elements that maps to the values in {@code STEPS}
   */
  private static ItemStack[] generateItems(String name, NamedTextColor color, ItemStack base) {
    ItemStack[] out = new ItemStack[6];
    Component nameComp = Component.text(name, color).decoration(TextDecoration.ITALIC, false);
    ItemMeta meta = base.getItemMeta();
    for (int i = 0; i < 6; i++) {
      int step = STEPS[i];
      meta.displayName(nameComp.append(Component.space()).append(Component.text("%+d".formatted(step))));
      out[i] = base.clone();
      out[i].setAmount(Math.abs(step));
      out[i].setItemMeta(meta);
    }
    return out;
  }

  static {
    // @formatter:off
    // TODO: Language
    RED   = generateItems("Red",   NamedTextColor.RED,   CustomHead.RED.asItemStack());
    GREEN = generateItems("Green", NamedTextColor.GREEN, CustomHead.GREEN.asItemStack());
    BLUE  = generateItems("Blue",  NamedTextColor.AQUA,  CustomHead.AQUA.asItemStack());
    // @formatter:on

    // Generate the rainbow button
    RAINBOW = CustomHead.RAINBOW.asItemStack();
    ItemMeta meta = RAINBOW.getItemMeta();
    // TODO: Language
    meta.displayName(MiniMessageWrapper.standard().mmParse("<rainbow>Random Hexadecimal Color</rainbow>").decoration(TextDecoration.ITALIC, false));
    RAINBOW.setItemMeta(meta);

    BACK_BUTTON = new ItemStack(Material.NETHER_STAR);
    meta = BACK_BUTTON.getItemMeta();
    // TODO: Language
    meta.displayName(Component.text("Back", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
    BACK_BUTTON.setItemMeta(meta);
  }

  public NickColorHexGui() {
    // TODO: Language
    this(0x88, 0x88, 0x88);
  }

  public NickColorHexGui(int r, int g, int b) {
    super(54, Component.text("Nick Color - ").append(Component.text(TextColor.color(r, g, b).toString(), TextColor.color(r, g, b))));

    this.red = r;
    this.green = g;
    this.blue = b;
  }

  /**
   * Update the name icon in the gui (name tag in slot 20)
   */
  private void updateName(boolean newGui) {
    TextColor col = TextColor.color(this.red, this.green, this.blue);
    ItemStack s = new ItemStack(Material.NAME_TAG);
    ItemMeta meta = s.getItemMeta();
    meta.displayName(Component.text(this.nickname).color(col).decoration(TextDecoration.ITALIC, false));
    meta.lore(List.of(Component.text(col.toString()).decoration(TextDecoration.ITALIC, false)));
    s.setItemMeta(meta);
    addActionButton(20, s, () -> {
      // Use the command since it already handles the logic
      this.user().performCommand("hexnicks:nickcolor <" + col + ">");
    });

    if (newGui) {
      // unfortunately, this seems to be the only way to change the title (without manual packets)
      new NickColorHexGui(this.red, this.green, this.blue).openGui(this.user());
    }
  }

  @Override
  protected void fillInventory() {
    this.blankInventory();
    // By this point, we have the player, so we can get their nickname
    this.nickname = PlainTextComponentSerializer.plainText().serialize(HexNicks.core().getDisplayName(this.user()));

    // Remove nickname prefix if essentials is hooked
    if (HexNicks.hooks().isEssentialsHooked()) {
      String nickPrefix = HexNicks.hooks().getEssNickPrefix();
      if (nickPrefix != null && this.nickname.startsWith(nickPrefix)) {
        this.nickname = this.nickname.substring(nickPrefix.length());
      }
    }

    // Add the "random hex colour" button
    addActionButton(49, RAINBOW, (a) -> {
      byte[] bytes = new byte[3];
      RNG.nextBytes(bytes);
      this.red = bytes[0];
      this.green = bytes[1];
      this.blue = bytes[2];
      updateName(true);
    });

    // Set the back button to the bottom-left
    addActionButton(45, BACK_BUTTON, (a) -> {
      new NickColorGui().openGui(this.user());
    });

    // Generate the nametag item
    updateName(false);

    // add all 18 buttons
    for (int i = 0; i < 6; i++) {
      final int finalI = i;
      // Add red column
      addActionButton(5 + i * 9, RED[i], (a) -> {
        if ((finalI == 0 || finalI == 5) && a == InventoryAction.MOVE_TO_OTHER_INVENTORY) { // shift click on +/- 16
          this.red = STEPS[finalI] < 0 ? 0x00 : 0xff;
        } else {
          this.red = MiscUtils.constrain(this.red + STEPS[finalI], 0, 255);
        }
        updateName(true);
      });

      // Add green column
      addActionButton(6 + i * 9, GREEN[i], (a) -> {
        if ((finalI == 0 || finalI == 5) && a == InventoryAction.MOVE_TO_OTHER_INVENTORY) { // shift click on +/- 16
          this.green = STEPS[finalI] < 0 ? 0x00 : 0xff;
        } else {
          this.green = MiscUtils.constrain(this.green + STEPS[finalI], 0, 255);
        }
        updateName(true);
      });

      // Add blue column
      addActionButton(7 + i * 9, BLUE[i], (a) -> {
        if ((finalI == 0 || finalI == 5) && a == InventoryAction.MOVE_TO_OTHER_INVENTORY) { // shift click on +/- 16
          this.blue = STEPS[finalI] < 0 ? 0x00 : 0xff;
        } else {
          this.blue = MiscUtils.constrain(this.blue + STEPS[finalI], 0, 255);
        }
        updateName(true);
      });
    }
  }

}
