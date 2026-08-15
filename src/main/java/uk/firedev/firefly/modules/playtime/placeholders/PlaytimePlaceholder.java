package uk.firedev.firefly.modules.playtime.placeholders;

import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.firefly.modules.playtime.PlaytimeModule;
import uk.firedev.firefly.placeholders.FireflyPlaceholder;

public class PlaytimePlaceholder extends FireflyPlaceholder {

    public PlaytimePlaceholder(@NonNull PlaytimeModule module) {
        super(module);
    }

    @Override
    public @Nullable String parse(@NonNull OfflinePlayer player) {
        return ((PlaytimeModule) module).getTimeFormatted(player);
    }

    @Override
    public boolean shouldProcess(@NonNull String identifier) {
        return identifier.equals("playtime");
    }

}
