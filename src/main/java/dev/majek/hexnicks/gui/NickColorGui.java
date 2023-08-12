package dev.majek.hexnicks.gui;

import dev.majek.hexnicks.command.CommandHexNicks;
import dev.majek.hexnicks.command.CommandNickColor;
import dev.majek.hexnicks.util.CustomHead;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A gui to be used for choosing a nick color.  This shows the vanilla colours (black, dark blue, etc.) and a button to create a hexadecimal color.
 */
public class NickColorGui extends Gui {

  private static final ItemStack BLACK;
  private static final ItemStack DARK_BLUE;
  private static final ItemStack DARK_GREEN;
  private static final ItemStack DARK_AQUA;
  private static final ItemStack DARK_RED;
  private static final ItemStack DARK_PURPLE;
  private static final ItemStack GOLD;
  private static final ItemStack GRAY;
  private static final ItemStack DARK_GRAY;
  private static final ItemStack BLUE;
  private static final ItemStack GREEN;
  private static final ItemStack AQUA;
  private static final ItemStack RED;
  private static final ItemStack LIGHT_PURPLE;
  private static final ItemStack YELLOW;
  private static final ItemStack WHITE;

  private static final ItemStack RAINBOW;

  /**
   * Generate an item to represent a color choice
   *
   * @param name  the name of the colour
   * @param color the color to use for the title (probably the color that this item represents)
   * @param base  the base item stack to use -- this is _not_ cloned
   * @return The {@link ItemStack} that can be used to represent this colour
   */
  @Contract(value = "_, _, _ -> param3", mutates = "param3")
  private static ItemStack generateItem(@NotNull String name, @NotNull TextColor color, @NotNull ItemStack base) {
    Component nameComp = Component.text(name, color, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false);
    ItemMeta meta = base.getItemMeta();
    meta.displayName(nameComp);
    base.setItemMeta(meta);
    return base;
  }

  static {
    // @formatter:off
    // TODO: Language(?)
    BLACK        = generateItem("Black",        NamedTextColor.BLACK,        CustomHead.BLACK.asItemStack()        );
    DARK_BLUE    = generateItem("Dark Blue",    NamedTextColor.DARK_BLUE,    CustomHead.DARK_BLUE.asItemStack()    );
    DARK_GREEN   = generateItem("Dark Green",   NamedTextColor.DARK_GREEN,   CustomHead.DARK_GREEN.asItemStack()   );
    DARK_AQUA    = generateItem("Dark Aqua",    NamedTextColor.DARK_AQUA,    CustomHead.DARK_AQUA.asItemStack()    );
    DARK_RED     = generateItem("Dark Red",     NamedTextColor.DARK_RED,     CustomHead.DARK_RED.asItemStack()     );
    DARK_PURPLE  = generateItem("Dark Purple",  NamedTextColor.DARK_PURPLE,  CustomHead.DARK_PURPLE.asItemStack()  );
    GOLD         = generateItem("Gold",         NamedTextColor.GOLD,         CustomHead.GOLD.asItemStack()         );
    GRAY         = generateItem("Gray",         NamedTextColor.GRAY,         CustomHead.GRAY.asItemStack()         );
    DARK_GRAY    = generateItem("Dark Gray",    NamedTextColor.DARK_GRAY,    CustomHead.DARK_GRAY.asItemStack()    );
    BLUE         = generateItem("Blue",         NamedTextColor.BLUE,         CustomHead.BLUE.asItemStack()         );
    GREEN        = generateItem("Green",        NamedTextColor.GREEN,        CustomHead.GREEN.asItemStack()        );
    AQUA         = generateItem("Aqua",         NamedTextColor.AQUA,         CustomHead.AQUA.asItemStack()         );
    RED          = generateItem("Red",          NamedTextColor.RED,          CustomHead.RED.asItemStack()          );
    LIGHT_PURPLE = generateItem("Light Purple", NamedTextColor.LIGHT_PURPLE, CustomHead.LIGHT_PURPLE.asItemStack() );
    YELLOW       = generateItem("Yellow",       NamedTextColor.YELLOW,       CustomHead.YELLOW.asItemStack()       );
    WHITE        = generateItem("White",        NamedTextColor.WHITE,        CustomHead.WHITE.asItemStack()        );
    // @formatter:on

    RAINBOW = CustomHead.RAINBOW.asItemStack();
    ItemMeta meta = RAINBOW.getItemMeta();
    // TODO: Language
    meta.displayName(Component.text("Hexadecimal Color").decoration(TextDecoration.ITALIC, false));
    meta.lore(List.of(
      // TODO: Language
      Component.text("Create a custom hexadecimal color", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
    ));
    RAINBOW.setItemMeta(meta);
  }

  public NickColorGui() {
    // TODO: Language
    super(54, Component.text("Nick Color"));
  }

  /**
   * Generate a function that may be used as an action for an item to set the nick color to a value.
   *
   * @param color The color to which the nickname should be set
   * @return The Runnable that can be used as an action
   */
  private Runnable setNickColor(TextColor color) {
    return () -> {
      // Use the command since it already handles the logic
      this.user().performCommand("hexnicks:nickcolor <" + color + ">");
    };
  }

  @Override
  protected void fillInventory() {
    this.blankInventory();

    addActionButton(10, DARK_BLUE, setNickColor(NamedTextColor.DARK_BLUE));
    addActionButton(11, DARK_GREEN, setNickColor(NamedTextColor.DARK_GREEN));
    addActionButton(12, DARK_AQUA, setNickColor(NamedTextColor.DARK_AQUA));
    addActionButton(13, DARK_RED, setNickColor(NamedTextColor.DARK_RED));
    addActionButton(14, DARK_PURPLE, setNickColor(NamedTextColor.DARK_PURPLE));
    addActionButton(15, GOLD, setNickColor(NamedTextColor.GOLD));
    addActionButton(16, GRAY, setNickColor(NamedTextColor.GRAY));

    addActionButton(19, BLUE, setNickColor(NamedTextColor.BLUE));
    addActionButton(20, GREEN, setNickColor(NamedTextColor.GREEN));
    addActionButton(21, AQUA, setNickColor(NamedTextColor.AQUA));
    addActionButton(22, RED, setNickColor(NamedTextColor.RED));
    addActionButton(23, LIGHT_PURPLE, setNickColor(NamedTextColor.LIGHT_PURPLE));
    addActionButton(24, YELLOW, setNickColor(NamedTextColor.YELLOW));
    addActionButton(25, WHITE, setNickColor(NamedTextColor.WHITE));

    addActionButton(29, BLACK, setNickColor(NamedTextColor.BLACK));
    addActionButton(33, DARK_GRAY, setNickColor(NamedTextColor.DARK_GRAY));

    addActionButton(40, RAINBOW, (a) -> {
      new NickColorHexGui().openGui(this.user());
    });
  }

}
