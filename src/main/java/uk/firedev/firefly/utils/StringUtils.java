package uk.firedev.firefly.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jspecify.annotations.NonNull;

public class StringUtils {

    private static final MiniMessage miniMessage = MiniMessage.builder()
            .tags(TagResolver.resolver(
                StandardTags.color(),
                StandardTags.decorations(),
                StandardTags.gradient(),
                StandardTags.rainbow()
            ))
            .build();
    private static final LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    /**
     * Gets a component from a provided String.
     * Only parses color and decoration.
     * <p>
     * This supports Legacy colors for user input.
     * Either format may be used, but MiniMessage should be preferred.
     * @param string The string to convert
     * @return A Component built from the String
     */
    public static Component getColorOnlyComponent(@NonNull String string) {
        if (miniMessage.stripTags(string).equals(string)) {
            return legacyComponentSerializer.deserialize(string);
        }
        return miniMessage.deserialize(string);
    }

    public static MiniMessage getColorOnlyMiniMessage() {
        return miniMessage;
    }

}
