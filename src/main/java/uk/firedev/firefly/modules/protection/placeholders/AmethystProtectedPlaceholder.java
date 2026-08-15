package uk.firedev.firefly.modules.protection.placeholders;

import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.firefly.modules.protection.protections.AmethystProtection;
import uk.firedev.firefly.placeholders.FireflyPlaceholder;

public class AmethystProtectedPlaceholder extends FireflyPlaceholder {

    public AmethystProtectedPlaceholder(@NonNull AmethystProtection module) {
        super(module);
    }

    @Override
    public @Nullable String parse(@NonNull OfflinePlayer player) {
        boolean value = ((AmethystProtection) module).isEnabled(player);
        return String.valueOf(value);
    }

    @Override
    public boolean shouldProcess(@NonNull String identifier) {
        return identifier.equals("amethyst_protected");
    }

}
