package uk.firedev.firefly.modules.playtime;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.addons.requirement.RequirementAddon;
import uk.firedev.daisylib.addons.requirement.RequirementData;
import uk.firedev.daisylib.util.Utils;
import uk.firedev.firefly.Firefly;

import java.util.List;

public class PlaytimeRequirement extends RequirementAddon {

    @Override
    public boolean checkRequirement(@NonNull RequirementData data, @NonNull List<String> values) {
        Player player = data.getPlayer();
        if (player == null) {
            return false;
        }
        for (String value : values) {
            if (!Utils.isLong(value)) {
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
    public @NonNull String getKey() {
        return "Playtime";
    }

    @Override
    public @NonNull String getAuthor() {
        return "FireML";
    }

    @Override
    public @NonNull Plugin getPlugin() {
        return Firefly.getInstance();
    }

}
