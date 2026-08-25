package uk.firedev.firefly.modules.titles.placeholders;

import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.firefly.modules.titles.TitleModule;
import uk.firedev.firefly.placeholders.FireflyPlaceholder;

public class PlayerPrefixPlaceholder extends FireflyPlaceholder {

    public PlayerPrefixPlaceholder(@NonNull TitleModule module) {
        super(module);
    }

    @Override
    public @Nullable String parse(@NonNull OfflinePlayer player) {
        return ((TitleModule) module).getPlayerPrefixLegacy(player);
    }

    @Override
    public boolean shouldProcess(@NonNull String identifier) {
        return identifier.equals("player_prefix");
    }

}
