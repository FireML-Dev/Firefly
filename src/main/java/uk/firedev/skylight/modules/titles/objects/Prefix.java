package uk.firedev.skylight.modules.titles.objects;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.message.component.ComponentMessage;
import uk.firedev.skylight.Skylight;
import uk.firedev.skylight.modules.titles.TitleManager;

import java.util.logging.Level;

public class Prefix implements TitlePart {

    private final @NotNull ConfigurationSection section;
    private final @NotNull Component display;
    private final @NotNull String permission;

    public Prefix(@NotNull ConfigurationSection section) throws InvalidConfigurationException {
        String displayString = section.getString("display");
        if (displayString == null) {
            throw new InvalidConfigurationException("No display name found for prefix " + section.getCurrentPath());
        }
        String permission = section.getString("permission");
        if (permission == null) {
            String defaultPermission = "skylight.prefix." + section.getName();
            Loggers.warn(Skylight.getInstance().getComponentLogger(),
                    "No permission found for prefix " + section.getCurrentPath() + ". Defaulting to " + defaultPermission
            );
            permission = defaultPermission;
        }
        this.section = section;
        this.display = new ComponentMessage(displayString).getMessage();
        this.permission = permission;
    }

    @Override
    public void apply(@NotNull Player player) {
        TitleManager.getInstance().setPlayerPrefix(player, this);
    }

    @Override
    public @NotNull Component getDisplay() {
        return display;
    }

    @Override
    public @NotNull String getPermission() {
        return permission;
    }

    @Override
    public @NotNull ConfigurationSection getConfigurationSection() {
        return section;
    }

    @Override
    public boolean isPrefix() {
        return true;
    }

    @Override
    public boolean isSuffix() {
        return false;
    }

}
