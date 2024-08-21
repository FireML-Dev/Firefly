package uk.firedev.firefly.utils;

import net.Zrips.CMILib.Colors.CMIChatColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public class StringUtils {

    /**
     * Gets a component from a provided legacy color code String.
     * @param string The string to convert
     * @return A Component built from the String
     */
    @SuppressWarnings("deprecation")
    public static Component getComponent(@NotNull String string) {
        // Do yucky legacy color stuff
        if (Bukkit.getPluginManager().isPluginEnabled("CMILib")) {
            string = CMIChatColor.colorize(string);
        } else {
            string = ChatColor.translateAlternateColorCodes('&', string);
        }
        return LegacyComponentSerializer.legacySection().deserialize(string);
    }

}
