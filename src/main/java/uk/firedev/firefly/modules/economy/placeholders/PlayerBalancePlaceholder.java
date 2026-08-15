package uk.firedev.firefly.modules.economy.placeholders;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.firefly.database.PlayerData;
import uk.firedev.firefly.modules.economy.EconomyModule;
import uk.firedev.firefly.placeholders.PlayerDataPlaceholder;

public class PlayerBalancePlaceholder extends PlayerDataPlaceholder {

    public PlayerBalancePlaceholder(@NonNull EconomyModule module) {
        super(module);
    }

    @Override
    public boolean shouldProcess(@NonNull String identifier) {
        return identifier.equals("player_balance");
    }

    @Override
    public @Nullable String parse(@NonNull PlayerData playerData) {
        return String.valueOf(playerData.getBalance());
    }

}
