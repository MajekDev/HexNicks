package dev.majek.hexnicks.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public enum CustomHead {
    BLACK("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTc0ZmU5Y2I4MDAyOWQ2NjM0NTI3N2FhNTYwZDQxZWYxMDMwOTYyYjdmMjlhYmYyMzk2MWQ5ZWJhODQyNTBhMyJ9fX0="),
    DARK_BLUE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2U3YWI3MTJjODdmNjdkNDhiOThmNzA2MzRkMWRjZmNkNTk4MGMzZDZmMGQ2MjJjZGMzMjMwOTEyMzYxYjU0ZSJ9fX0="),
    DARK_GREEN("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTNlOWY0ZGJhZGRlMGY3MjdjNTgwM2Q3NWQ4YmIzNzhmYjlmY2I0YjYwZDMzYmVjMTkwOTJhM2EyZTdiMDdhOSJ9fX0="),
    DARK_AQUA("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTc1YjdhYzlmMGM3MTIzMDNjZDNiNjU0ZTY0NmNlMWM0YmYyNDNhYjM0OGE2YTI1MzcwZjI2MDNlNzlhNjJhMCJ9fX0="),
    DARK_RED("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY1ZjNiYWUwZDIwM2JhMTZmZTFkYzNkMTMwN2E4NmE2MzhiZTkyNDQ3MWYyM2U4MmFiZDlkNzhmOGEzZmNhIn19fQ=="),
    DARK_PURPLE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDY3ZjJiNTA2MzcwYzFlODRmOTBmYmYyOWM4MGUwY2I3ZTJhYzkzMjMwMzAxYjVkOGU0MmM2OGZkZGU4OWZlMCJ9fX0="),
    GOLD("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTE4OWYzNDdmNDI0NTBjZDJhMmU5YjhhNTM5ODgwN2QyOGM3ZjQyNTRiZDk5YThhNDk5Y2U1NDM1MzIwOTU1In19fQ=="),
    GRAY("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzMyOGRjZGUxNzNiZWZmOWYzZjQxYjkyMzIxM2ZjMWJiNzY3ODk2N2NjYjJlZGU3YTdjZjQwYjE4MzZiMWE3MyJ9fX0="),
    DARK_GRAY("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2FmNmZhYjc2N2NhNGQ3ZGY2MjE3Yjg5NWI2NjdiY2FjYzUyNGQ0MDcwNjg2MTlmODE5YTA3MGYzZjYyOWNlMCJ9fX0="),
    BLUE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2I1MTA2YjA2MGVhZjM5ODIxNzM0OWYzY2ZiNGYyYzdjNGZkOWEwYjAzMDdhMTdlYmE2YWY3ODg5YmUwZmJlNiJ9fX0="),
    GREEN("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjk4NWEyOTk1N2Q0MGZhNTY0ZDVlMzFjYmQ5MDVlMzY5NGE2MTYzOTNjZTEzNzEwYmZjMzFiMWI4YjBhNTIyZCJ9fX0="),
    AQUA("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjllMTY5NzkzMDliNWE5YjY3M2Q2MGQxMzkwYmJhYjBkMDM4NWVhYzcyNTRkODI4YWRhMmEzNmE0NmY3M2E1OSJ9fX0="),
    RED("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjA2MmQ4ZDcyZjU4OTFjNzFmYWIzMGQ1MmUwNDgxNzk1YjNkMmQzZDJlZDJmOGI5YjUxN2Q3ZDI4MjFlMzVkNiJ9fX0="),
    LIGHT_PURPLE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E5ZWE2ZTM2ZjllNTc5ZjU4NmFkYjE5MzdiYjE0Mzc3YjBkNzQwMzRmZmNiMjU1NmEyYWNiNDM1NjcxNDQ4ZiJ9fX0="),
    YELLOW("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjAwYmY0YmYxNGM4Njk5YzBmOTIwOWNhNzlmZTE4MjUzZTkwMWU5ZWMzODc2YTJiYTA5NWRhMDUyZjY5ZWJhNyJ9fX0="),
    WHITE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGUwZThhY2FiYWQyN2Q0NjE2ZmFlOWU0NzJjMGRlNjA4NTNkMjAzYzFjNmYzMTM2N2M5MzliNjE5ZjNlMzgzMSJ9fX0="),

    RAINBOW("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg3ZmQyM2E3ODM2OWJkMzg3NWRhODg5NmYxNTBjNGFmOWYyMzM3NGUwNDhlMzA5MTM5MDBlM2ZkZDc3ODU5YSJ9fX0="),
    ;

    private final ItemStack itemStack;

    CustomHead(String base64) {
        this.itemStack = getHead(base64);
    }

    public ItemStack asItemStack() {
        return this.itemStack.clone();
    }

    private static ItemStack getHead(String base64) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (base64 == null || base64.isEmpty()) {
            return head;
        }
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        profile.setProperty(new ProfileProperty("textures", base64));
        headMeta.setPlayerProfile(profile);
        head.setItemMeta(headMeta);
        return head;
    }
}
