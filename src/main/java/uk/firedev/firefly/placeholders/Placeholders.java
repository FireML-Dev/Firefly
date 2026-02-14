package uk.firedev.firefly.placeholders;

import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.placeholder.PlaceholderProvider;
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
        if (provider != null) {
            return;
        }
        provider = new PlaceholderProvider(Firefly.getInstance());
    }

    /**
     * Allows registration of placeholders.
     * This will not do anything after the plugin/module's first load.
     */
    public static void manageProvider(@NonNull Consumer<PlaceholderProvider> consumer) {
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
