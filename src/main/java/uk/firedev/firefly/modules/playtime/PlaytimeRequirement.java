package uk.firedev.firefly.modules.playtime;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.addons.requirement.RequirementAddon;
import uk.firedev.daisylib.addons.requirement.RequirementData;
import uk.firedev.daisylib.utils.ObjectUtils;
import uk.firedev.firefly.Firefly;

import java.util.List;

public class PlaytimeRequirement extends RequirementAddon {

    @Override
    public boolean checkRequirement(@NotNull RequirementData data, @NotNull List<String> values) {
        Player player = data.getPlayer();
        if (player == null) {
            return false;
        }
        for (String value : values) {
            if (!ObjectUtils.isLong(value)) {
                return false;
            }
            long playtimeNeeded = Long.parseLong(value);
            if (PlaytimeModule.getInstance().getTime(player) >= playtimeNeeded) {
                return true;
            }
        }
        return false;
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
    public @NotNull Plugin getOwningPlugin() {
        return Firefly.getInstance();
    }

}
