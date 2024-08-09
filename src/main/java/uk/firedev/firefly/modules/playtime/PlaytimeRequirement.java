package uk.firedev.firefly.modules.playtime;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.requirement.RequirementType;
import uk.firedev.daisylib.utils.ObjectUtils;
import uk.firedev.firefly.Firefly;

public class PlaytimeRequirement implements RequirementType {

    @Override
    public boolean checkRequirement(@NotNull Player player, @NotNull String value) {
        if (!ObjectUtils.isLong(value)) {
            return false;
        }
        long playtimeNeeded = Long.parseLong(value);
        return PlaytimeManager.getInstance().getTime(player) >= playtimeNeeded;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "PLAYTIME";
    }

    @Override
    public @NotNull String getAuthor() {
        return "FireML";
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return Firefly.getInstance();
    }

}
