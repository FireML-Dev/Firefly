package uk.firedev.firefly.modules.kit.placeholders;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.daisylib.placeholders.IPlaceholder;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.modules.kit.Kit;
import uk.firedev.firefly.modules.kit.KitModule;

public class KitAvailablePlaceholder implements IPlaceholder {

    private static final String IDENTIFIER = "kit_available_";
    private static final int IDENTIFIER_LENGTH = IDENTIFIER.length();

    private final KitModule module;

    public KitAvailablePlaceholder(@NonNull KitModule module) {
        this.module = module;
    }

    @Override
    public boolean shouldProcess(@NonNull String identifier) {
        return identifier.startsWith(IDENTIFIER);
    }

    @Override
    public @Nullable String parse(@Nullable OfflinePlayer player, @NonNull String identifier) {
        if (player == null || !module.isConfigEnabled()) {
            return null;
        }
        String value = identifier.substring(IDENTIFIER_LENGTH);
        Kit kit = module.getKit(value);
        if (kit == null) {
            return value + " is not a valid kit.";
        }
        boolean available = kit.hasPermission(player) && !kit.isOnCooldown(player.getUniqueId());
        return String.valueOf(available);
    }

}
