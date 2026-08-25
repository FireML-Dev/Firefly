package uk.firedev.firefly.modules.kit;

import com.oheers.fish.api.reward.RewardType;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;

import org.jspecify.annotations.Nullable;
import uk.firedev.daisylib.logging.Logging;
import uk.firedev.firefly.Firefly;

public class KitRewardType extends RewardType {

    private static final Logging logging = Logging.logging(KitRewardType.class);

    @Override
    public void doReward(@NonNull Player player, @NonNull String key, @NonNull String value, @Nullable Location location) {
        Kit kit;
        try {
            kit = new Kit(value);
        } catch (InvalidConfigurationException exception) {
            logging.warn("Invalid kit specified: " + value);
            return;
        }
        kit.giveToPlayer(player, null);
    }

    @Override
    public @NonNull String getIdentifier() {
        return "Kit";
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
