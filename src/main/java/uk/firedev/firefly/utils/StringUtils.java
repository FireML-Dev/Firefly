package uk.firedev.firefly.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.jetbrains.annotations.NotNull;

public class StringUtils {

    private static MiniMessage miniMessage;

    /**
     * Gets a component from a provided String.
     * Only parses color and decoration tags.
     * @param string The string to convert
     * @return A Component built from the String
     */
    public static Component getColorOnlyComponent(@NotNull String string) {
        return getColorOnlyMiniMessage().deserialize(string);
    }

    public static MiniMessage getColorOnlyMiniMessage() {
        if (miniMessage == null) {
            miniMessage = MiniMessage
                    .builder()
                    .tags(StandardTags.color())
                    .tags(StandardTags.decorations())
                    .build();
        }
        return miniMessage;
    }

}
