package uk.firedev.firefly.placeholders;

import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.placeholders.PlaceholderProvider;
import uk.firedev.firefly.Firefly;

import java.util.function.Consumer;

/**
 * Registers the plugin's placeholders using DaisyLib's PlaceholderProvider class.
 * <p>
 * This registers placeholders for both MiniPlaceholders and PlaceholderAPI.
 */
public class Placeholders {

    private static PlaceholderProvider provider;

    public static void init() {
        provider = PlaceholderProvider.create(Firefly.getInstance());
    }

    /**
     * Allows registration of placeholders.
     * This will not do anything after the plugin's first load.
     * @param consumer
     */
    public static void manageProvider(@NotNull Consumer<PlaceholderProvider> consumer) {
        if (provider == null) {
            return;
        }
        consumer.accept(provider);
    }

    public static void register() {
        if (provider == null) {
            return;
        }
        provider.register();
        provider = null;
    }

}
