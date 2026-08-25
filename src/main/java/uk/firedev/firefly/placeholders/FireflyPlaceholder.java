package uk.firedev.firefly.placeholders;

import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.daisylib.placeholders.IPlaceholder;
import uk.firedev.firefly.SubModule;

public abstract class FireflyPlaceholder implements IPlaceholder {

    protected final SubModule module;

    public FireflyPlaceholder(@NonNull SubModule module) {
        this.module = module;
    }

    @Override
    public @Nullable String parse(@Nullable OfflinePlayer player, @NonNull String identifier) {
        if (player == null || !module.isConfigEnabled()) {
            return null;
        }
        return parse(player);
    }

    public abstract @Nullable String parse(@NonNull OfflinePlayer player);

}
