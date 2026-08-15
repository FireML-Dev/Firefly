package uk.firedev.firefly.modules.playtime.placeholders;

import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.firefly.modules.playtime.PlaytimeModule;
import uk.firedev.firefly.placeholders.FireflyPlaceholder;

public class PlaytimeRawPlaceholder extends FireflyPlaceholder {

    public PlaytimeRawPlaceholder(@NonNull PlaytimeModule module) {
        super(module);
    }

    @Override
    public @Nullable String parse(@NonNull OfflinePlayer player) {
        long time = ((PlaytimeModule) module).getTime(player);
        return String.valueOf(time);
    }

    @Override
    public boolean shouldProcess(@NonNull String identifier) {
        return identifier.equals("playtime_raw");
    }

}
