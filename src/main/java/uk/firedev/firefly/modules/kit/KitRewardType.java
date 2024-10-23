package uk.firedev.firefly.modules.kit;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.reward.RewardType;
import uk.firedev.firefly.Firefly;

public class KitRewardType implements RewardType {

    @Override
    public void doReward(@NotNull Player player, @NotNull String value) {
        Kit kit;
        try {
            kit = new Kit(value);
        } catch (InvalidConfigurationException exception) {
            Loggers.info(getComponentLogger(), "Invalid kit specified for RewardType " + getIdentifier() + ": " + value);
            return;
        }
        kit.giveToPlayer(player, null);
    }

    @Override
    public @NotNull String getIdentifier() {
        return "KIT";
    }

    @Override
    public @NotNull String getAuthor() {
        return "FireML";
    }

    @Override
    public @NotNull JavaPlugin getPlugin() {
        return Firefly.getInstance();
    }

}
