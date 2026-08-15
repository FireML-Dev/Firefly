package uk.firedev.firefly.modules.nickname.placeholders;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import uk.firedev.firefly.database.PlayerData;
import uk.firedev.firefly.modules.nickname.NicknameModule;
import uk.firedev.firefly.placeholders.PlayerDataPlaceholder;

public class PlayerNicknamePlaceholder extends PlayerDataPlaceholder {

    public PlayerNicknamePlaceholder(@NonNull NicknameModule module) {
        super(module);
    }

    @Override
    public @Nullable String parse(@NonNull PlayerData playerData) {
        return LegacyComponentSerializer.legacySection().serialize(playerData.getNickname()); // Legacy is DISGUSTING but PlaceholderAPI is dumb so gotta.
    }

    @Override
    public boolean shouldProcess(@NonNull String identifier) {
        return identifier.equals("player_nickname");
    }

}
