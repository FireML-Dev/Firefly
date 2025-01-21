package uk.firedev.firefly.modules.titles.objects;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.libs.boostedyaml.block.implementation.Section;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.modules.titles.TitleModule;

public class Suffix implements TitlePart {

    private final @NotNull Section section;
    private final @NotNull ComponentMessage display;
    private final @NotNull String permission;

    public Suffix(@NotNull Section section) throws InvalidConfigurationException {
        String displayString = section.getString("display");
        if (displayString == null) {
            throw new InvalidConfigurationException("No display name found for suffix " + section.getNameAsRoute());
        }
        String permission = section.getString("permission");
        if (permission == null) {
            String defaultPermission = "firefly.suffix." + section.getName();
            Loggers.warn(Firefly.getInstance().getComponentLogger(),
                    "No permission found for suffix " + section.getNameAsRoute() + ". Defaulting to " + defaultPermission
            );
            permission = defaultPermission;
        }
        this.section = section;
        this.display = ComponentMessage.fromString(displayString);
        this.permission = permission;
    }

    @Override
    public void apply(@NotNull Player player) {
        TitleModule.getInstance().setPlayerSuffix(player, this);
    }

    @Override
    public @NotNull ComponentMessage getDisplay() {
        return display;
    }

    @Override
    public @NotNull String getPermission() {
        return permission;
    }

    @Override
    public @NotNull Section getSection() {
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
