package uk.firedev.skylight.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class StringUtils {

    /**
     * Gets a component from a provided String.
     * Supports legacy and MiniMessage
     * @param string The string to convert
     * @return A Component built from the String
     */
    public static Component getComponent(@NotNull String string) {
        string = string.replace('&', '§');
        Component component;
        try {
            component = MiniMessage.miniMessage().deserialize(string);
        } catch (ParsingException exception) {
            component = LegacyComponentSerializer.legacySection().deserialize(string);
        }
        return component;
    }

}
