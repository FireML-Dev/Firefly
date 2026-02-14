package uk.firedev.firefly.modules.titles.objects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.util.Loggers;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.modules.titles.TitleModule;
import uk.firedev.daisylib.libs.messagelib.message.ComponentMessage;
import uk.firedev.daisylib.libs.messagelib.message.ComponentSingleMessage;

public class Suffix implements TitlePart {

    private final @NonNull ConfigurationSection section;
    private final @NonNull ComponentSingleMessage display;
    private final @NonNull String permission;

    public Suffix(@NonNull ConfigurationSection section) throws InvalidConfigurationException {
        String displayString = section.getString("display");
        if (displayString == null) {
            throw new InvalidConfigurationException("No display name found for suffix " + section.getName());
        }
        String permission = section.getString("permission");
        if (permission == null) {
            String defaultPermission = "firefly.suffix." + section.getName();
            Loggers.warn(Firefly.getInstance().getComponentLogger(),
                    "No permission found for suffix " + section.getName() + ". Defaulting to " + defaultPermission
            );
            permission = defaultPermission;
        }
        this.section = section;
        this.display = ComponentMessage.componentMessage(displayString);
        this.permission = permission;
    }

    @Override
    public void apply(@NonNull Player player) {
        TitleModule.getInstance().setPlayerSuffix(player, this);
    }

    @Override
    public @NonNull ComponentSingleMessage getDisplay() {
        return display;
    }

    @Override
    public @NonNull String getPermission() {
        return permission;
    }

    @Override
    public @NonNull ConfigurationSection getSection() {
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
