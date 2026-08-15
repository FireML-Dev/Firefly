package uk.firedev.firefly.placeholders;

import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.firefly.Module;
import uk.firedev.firefly.database.PlayerData;

public abstract class PlayerDataPlaceholder extends FireflyPlaceholder {

    public PlayerDataPlaceholder(@NonNull Module module) {
        super(module);
    }

    @Override
    public @Nullable String parse(@Nullable OfflinePlayer player, @NonNull String identifier) {
        if (player == null || !module.isConfigEnabled()) {
            return null;
        }
        return parse(player);
    }

    @Override
    public @Nullable String parse(@NonNull OfflinePlayer player) {
        return parse(PlayerData.playerData(player.getUniqueId()));
    }

    public abstract @Nullable String parse(@NonNull PlayerData playerData);

}
