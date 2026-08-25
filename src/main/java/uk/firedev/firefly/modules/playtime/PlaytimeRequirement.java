package uk.firedev.firefly.modules.playtime;

import com.oheers.fish.api.requirement.RequirementContext;
import com.oheers.fish.api.requirement.RequirementType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.utils.CommonUtils;
import uk.firedev.firefly.Firefly;

import java.util.List;

public class PlaytimeRequirement extends RequirementType {

    @Override
    public boolean checkRequirement(@NonNull RequirementContext context, @NonNull List<String> values) {
        Player player = context.getPlayer();
        if (player == null) {
            return false;
        }
        for (String value : values) {
            Long parsed = CommonUtils.getLong(value);
            if (parsed == null) {
                return false;
            }
            if (PlaytimeModule.getInstance().getTime(player) >= parsed) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @NonNull String getIdentifier() {
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
