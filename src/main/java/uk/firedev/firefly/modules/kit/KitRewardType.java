package uk.firedev.firefly.modules.kit;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.daisylib.api.addons.reward.RewardAddon;
import uk.firedev.firefly.Firefly;

public class KitRewardType extends RewardAddon {

    @Override
    public void doReward(@NotNull Player player, @NotNull String value) {
        Kit kit;
        try {
            kit = new Kit(value);
        } catch (InvalidConfigurationException exception) {
            Loggers.info(getClass(), "Invalid kit specified: " + value);
            return;
        }
        kit.giveToPlayer(player, null);
    }

    @Override
    public @NotNull String getIdentifier() {
        return "Kit";
    }

    @Override
    public @NotNull String getAuthor() {
        return "FireML";
    }

    @Override
    public @NotNull JavaPlugin getOwningPlugin() {
        return Firefly.getInstance();
    }

}
