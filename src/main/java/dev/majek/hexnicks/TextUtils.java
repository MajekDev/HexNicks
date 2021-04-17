package dev.majek.hexnicks;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

    /** List of all valid color characters */
    public static final List<Character> COLOR_CHARS = Arrays.asList('0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'l', 'm', 'n', 'o', 'r', 'x');
    /** Pattern matching "nicer" legacy hex chat color codes - &#rrggbb */
    private static final Pattern HEX_COLOR_PATTERN_SIX = Pattern.compile("&#([0-9a-fA-F]{6})");
    /** Pattern matching "nicer" 3 character hex chat color codes - &#rgb */
    private static final Pattern HEX_COLOR_PATTERN_THREE = Pattern.compile("&#([0-9a-fA-F]{3})");

    /**
     * This will translate 6 character and 3 character hex codes (&#rrggbb and &#rgb)
     * and standard minecraft color codes (ex. &a, &7, etc.) to color.
     *
     * @param string The string to colorize.
     * @param blockDarkColors Whether or not to disallow colors that are too dark.
     * @return Formatted string.
     */
    public static String applyColorCodes(String string, boolean blockDarkColors) {
        // Do this first so it doesn't affect &0 in hex codes since they haven't been formatted
        if (blockDarkColors)
            string = string.replace("&0", "");

        // Do 6 char first since the 3 char pattern will also match 6 char occurrences
        StringBuffer sb6 = new StringBuffer();
        Matcher matcher6 = HEX_COLOR_PATTERN_SIX.matcher(string);
        while (matcher6.find()) {
            StringBuilder replacement = new StringBuilder(14).append("&x");
            for (char character : matcher6.group(1).toCharArray())
                replacement.append('&').append(character);
            if (blockDarkColors && getLuminescence(replacement.toString()) < 16)
                matcher6.appendReplacement(sb6, "");
            else
                matcher6.appendReplacement(sb6, replacement.toString());
        }
        matcher6.appendTail(sb6);
        string = sb6.toString();

        // Now convert 3 char to the same format ex. &#363 -> &x&3&3&6&6&3&3
        StringBuffer sb3 = new StringBuffer();
        Matcher matcher3 = HEX_COLOR_PATTERN_THREE.matcher(string);
        while (matcher3.find()) {
            StringBuilder replacement = new StringBuilder(14).append("&x");
            for (char character : matcher3.group(1).toCharArray())
                replacement.append('&').append(character).append('&').append(character);
            if (blockDarkColors && getLuminescence(replacement.toString()) < 16)
                matcher3.appendReplacement(sb3, "");
            else
                matcher3.appendReplacement(sb3, replacement.toString());
        }
        matcher3.appendTail(sb3);

        // Translate '&' to '§' (all standard color codes done here)
        return ChatColor.translateAlternateColorCodes('&', sb3.toString());
    }

    /**
     * Apply color codes using {@link TextUtils#applyColorCodes(String, boolean)}
     * and defaulting blockDarkColors to false.
     *
     * @param string The string to colorize.
     * @return Formatted string.
     */
    public static String applyColorCodes(String string) {
        return applyColorCodes(string, false);
    }

    /**
     * Completely remove all color codes from a string.
     * This will strip all standard codes and hex codes.
     *
     * @param string The string to remove colors from.
     * @return Clean string.
     */
    public static String removeColorCodes(String string) {
        // Colorize it first to properly strip hex codes
        string = applyColorCodes(string);
        StringBuilder sb = new StringBuilder(string.length());
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            if (chars[i] == '&' || chars[i] == ChatColor.COLOR_CHAR &&
                    i < chars.length - 1 && COLOR_CHARS.contains(chars[i + 1])) {
                ++i;
                continue;
            }
            sb.append(chars[i]);
        }
        return sb.toString();
    }

    /**
     * Get luminescence of a formatted hex color - "&x&r&r&g&g&b&b".
     *
     * @param color The formatted hex string.
     * @return Luminescence value 0-255.
     */
    public static double getLuminescence(String color) {
        color = color.replace("&", "").replace("x", "");
        int red   = Integer.valueOf(color.substring(0,2), 16);
        int green = Integer.valueOf(color.substring(2,4), 16);
        int blue  = Integer.valueOf(color.substring(4,6), 16);
        return (0.2126 * red) + (0.7152 * green) + (0.0722 * blue);
    }

    /**
     * Get only the color codes from a given string. This includes hex codes.
     *
     * @param string The string to remove color codes from.
     * @return String of only color codes.
     */
    public static String getColorCodes(String string) {
        // Colorize it first to properly get hex codes
        string = applyColorCodes(string);
        string = string.replace("§", "&");
        StringBuilder sb = new StringBuilder(string.length());
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            if (chars[i] == '&' || chars[i] == ChatColor.COLOR_CHAR &&
                    i < chars.length - 1 && COLOR_CHARS.contains(chars[i + 1])) {
                sb.append(chars[i]).append(chars[i+1]);
                ++i;
            }
        }
        return sb.toString();
    }

    /**
     * Parse JSON for {@link MiniJSON} within the provided message.
     *
     * @param message The message to parse.
     * @return Component from the provided string.
     */
    public static Component parseExpression(String message) {
        int indexOfJSONStart = message.indexOf('$');
        if (indexOfJSONStart == -1)
            return getComponentText(message).asComponent();
        TextComponent.Builder finalComponent = Component.text();
        String previousColor = "";
        while (message.contains("$")) {
            if (message.indexOf("$") != 0) {
                if (message.charAt(message.indexOf("$") - 1) == '\\') {
                    message = StringUtils.replaceOnce(message, "\\$", "%dollaSignPlaceholder%");
                    continue;
                }
            }
            int JSONSectionStart = message.indexOf("$");
            //Bukkit.getConsoleSender().sendMessage("Length: " + message.length() + " Start: " + JSONSectionStart + 1);
            // TODO: Fix issue with index out of bounds if $ is at the end of the message
            String jsonString;
            if (message.length() == JSONSectionStart + 1)
                jsonString = null;
            else
                jsonString = getEnclosed(JSONSectionStart + 1, message).getFirst();
            // If there is incorrect syntax go to the next
            if (jsonString == null) {
                message = StringUtils.replaceOnce(message, "$", "\\$");
                continue;
            }
            finalComponent.append(getComponentText(previousColor + message.substring(0, JSONSectionStart).replace("%dollaSignPlaceholder%", "$")));
            previousColor = getColorCodes(message.substring(0, JSONSectionStart));

            MiniJSON miniJSON = new MiniJSON(jsonString);
            if (miniJSON.isValidMiniJSON())
                finalComponent.append(miniJSON.getComponent());

            message = message.substring(getEnclosed(JSONSectionStart + 1, message).getSecond());
        }
        finalComponent.append(getComponentText(previousColor + message.replace("%dollaSignPlaceholder%", "$")));
        return finalComponent.asComponent();
    }

    /**
     * Get a {@link TextComponent.Builder} formatted with color from a String containing
     * standard Minecraft color codes and hex codes (&#aabbcc).
     *
     * @param text The string with standard codes.
     * @return {@link TextComponent.Builder} formatted with color.
     */
    public static TextComponent.Builder getComponentText(String text) {
        text = applyColorCodes(text).replace("§", "&");
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text).toBuilder();
    }

    /**
     * Using this to pull JSON Objects and brackets plus text inside from strings. If the end of the string is
     * encountered before the bracket is closed off, then {null, -1} is returned.
     *
     * @param start  the start index.
     * @param string the string to get the enclosed value.
     * @return a pair, where the first value is the enclosed string and the second value is the index of the character
     * after the closing bracket of the enclosed string.
     */
    private static Pair<String, Integer> getEnclosed(int start, String string) {
        boolean curved = string.charAt(start) == '('; // ()s or {}s
        int depth = 1, i = start + 1;

        // Exits when there are no pairs of open brackets
        while (depth > 0) {
            // Avoid index out of bound errors
            if (i == string.length())
                return new Pair<>(null, -1);

            char c = string.charAt(i++);

            // We've closed off a pair
            if (c == (curved ? ')' : '}'))
                --depth;
                // We've started a pair
            else if (c == (curved ? '(' : '{'))
                ++depth;
        }
        // Return the stuff inside the brackets, and the index of the char after the last bracket
        return new Pair<>(string.substring(start, i), i);
    }

    /**
     * Check if a string is valid JSON.
     *
     * @param Json The string to check.
     * @return Whether or not the JSON is valid.
     */
    public static boolean isJson(String Json) {
        try {
            new org.json.JSONObject(Json);
        } catch (JSONException ex) {
            try {
                new org.json.JSONArray(Json);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Handy in text formatting I call MiniJSON. <a href="http://google.com">How to use</a>.
     */
    public static class MiniJSON {

        private final String function;
        private final String baseText;
        private final String functionText;
        private final String functionText2;

        public boolean valid = true;

        public List<String> validFunctions = new ArrayList<>(Arrays.asList("hover", "hover-text",
                "command", "run-command", "suggest-command", "link", "url", "open-url", "clipboard",
                "copy-to-clipboard", "hover-command", "hover-suggest"));

        public MiniJSON(String miniJSON) {
            if (miniJSON == null || getEnclosed(0, miniJSON).getFirst() == null) {
                function = null; baseText = null; functionText = null; functionText2 = "null";
            } else {
                String[] args = getEnclosed(0, miniJSON).getFirst().replace("{", "")
                        .replace("}", "").split(",");
                if (args.length < 3) {
                    valid = false;
                    throw new InvalidMiniJSONException("Invalid MiniJSON expression.");
                }
                if (!validFunctions.contains(args[0].toLowerCase())) {
                    function = null; baseText = null; functionText = null; functionText2 = "";
                } else {
                    function = args[0].toLowerCase(); baseText = args[1]; functionText = args[2];
                    if (args.length >= 4)
                        functionText2 = args[3];
                    else
                        functionText2 = "";
                }
            }
        }

        /**
         * Check if the object is valid for what we want.
         *
         * @return True or false.
         */
        public boolean isValidMiniJSON() {
            return function != null && baseText != null && functionText != null && valid;
        }

        /**
         * Get the MiniJSON object as a {@link Component} with the appropriate formatting and events.
         *
         * @return Valid {@link Component}.
         */
        public Component getComponent() {
            TextComponent.Builder component = getComponentText(baseText);
            switch (function) {
                case "hover":
                case "hover-text":
                    component.hoverEvent(HoverEvent.showText(getComponentText(functionText)));
                    break;
                case "command":
                case "run-command":
                    component.clickEvent(ClickEvent.runCommand(functionText));
                    break;
                case "suggest-command":
                    component.clickEvent(ClickEvent.suggestCommand(functionText));
                    break;
                case "link":
                case "url":
                case "open-url":
                    component.clickEvent(ClickEvent.openUrl(functionText));
                    break;
                case "clipboard":
                case "copy-to-clipboard":
                    component.clickEvent(ClickEvent.copyToClipboard(functionText));
                    break;
                case "hover-command":
                    component.hoverEvent(HoverEvent.showText(getComponentText(functionText)));
                    component.clickEvent(ClickEvent.runCommand(functionText2));
                    break;
                case "hover-suggest":
                    component.hoverEvent(HoverEvent.showText(getComponentText(functionText)));
                    component.clickEvent(ClickEvent.suggestCommand(functionText2));
            }
            return component.asComponent();
        }
    }

    /**
     * Thrown if there is invalid syntax in the {@link MiniJSON} object.
     */
    public static class InvalidMiniJSONException extends RuntimeException {
        public InvalidMiniJSONException(String exception) {
            super(exception);
        }
    }

}
