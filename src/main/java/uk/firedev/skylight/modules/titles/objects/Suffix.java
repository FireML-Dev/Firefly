package uk.firedev.skylight.modules.titles.objects;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.utils.ComponentUtils;
import uk.firedev.skylight.Skylight;
import uk.firedev.skylight.modules.titles.TitleManager;

import java.util.logging.Level;

public class Suffix implements TitlePart {

    private final @NotNull ConfigurationSection section;
    private final @NotNull Component display;
    private final @NotNull String permission;

    public Suffix(@NotNull ConfigurationSection section) throws InvalidConfigurationException {
        String displayString = section.getString("display");
        if (displayString == null) {
            throw new InvalidConfigurationException("No display name found for suffix " + section.getCurrentPath());
        }
        String permission = section.getString("permission");
        if (permission == null) {
            String defaultPermission = "skylight.suffix." + section.getName();
            Loggers.log(Level.WARNING, Skylight.getInstance().getLogger(),
                    "No permission found for suffix " + section.getCurrentPath() + ". Defaulting to " + defaultPermission
            );
            permission = defaultPermission;
        }
        this.section = section;
        this.display = ComponentUtils.deserializeString(displayString);
        this.permission = permission;
    }

    @Override
    public void apply(@NotNull Player player) {
        TitleManager.getInstance().setPlayerSuffix(player, this);
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
        return false;
    }

    @Override
    public boolean isSuffix() {
        return true;
    }

}
