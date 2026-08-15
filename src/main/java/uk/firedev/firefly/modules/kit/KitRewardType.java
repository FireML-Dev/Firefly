package uk.firedev.firefly.modules.kit;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import org.jspecify.annotations.Nullable;
import uk.firedev.daisylib.addons.reward.RewardAddon;
import uk.firedev.firefly.Firefly;

public class KitRewardType extends RewardAddon {

    @Override
    public void give(@NonNull Player player, @NonNull String value, @Nullable Location location) {
        Kit kit;
        try {
            kit = new Kit(value);
        } catch (InvalidConfigurationException exception) {
            getLogging().warn("Invalid kit specified: " + value);
            return;
        }
        kit.giveToPlayer(player, null);
    }

    @Override
    public @NonNull String getKey() {
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
