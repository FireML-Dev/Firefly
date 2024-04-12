package uk.firedev.skylight.utils;

import net.Zrips.CMILib.Colors.CMIChatColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class StringUtils {

    /**
     * Converts legacy color codes to an Adventure Component.
     * I dislike using legacy, however it's necessary for ease of use.
     * @param string The string to convert
     * @return A Component built from the legacy String
     */
    public static Component convertLegacyToAdventure(@NotNull String string) {
        if (Bukkit.getPluginManager().isPluginEnabled("CMILib")) {
            string = CMIChatColor.colorize(string);
        }
        return LegacyComponentSerializer.legacySection().deserialize(string.replace('&', '§'));
    }

}
