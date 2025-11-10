package uk.firedev.firefly.modules.kit;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.addons.reward.RewardAddon;
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
    public @NotNull String getKey() {
        return "Kit";
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
