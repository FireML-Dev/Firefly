package uk.firedev.firefly.modules.titles.objects;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.daisylib.libs.boostedyaml.block.implementation.Section;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.modules.titles.TitleModule;

public class Prefix implements TitlePart {

    private final @NotNull Section section;
    private final @NotNull Component display;
    private final @NotNull String permission;

    public Prefix(@NotNull Section section) throws InvalidConfigurationException {
        String displayString = section.getString("display");
        if (displayString == null) {
            throw new InvalidConfigurationException("No display name found for prefix " + section.getNameAsRoute());
        }
        String permission = section.getString("permission");
        if (permission == null) {
            String defaultPermission = "firefly.prefix." + section.getName();
            Loggers.warn(Firefly.getInstance().getComponentLogger(),
                    "No permission found for prefix " + section.getNameAsRoute() + ". Defaulting to " + defaultPermission
            );
            permission = defaultPermission;
        }
        this.section = section;
        this.display = ComponentMessage.fromString(displayString).getMessage();
        this.permission = permission;
    }

    @Override
    public void apply(@NotNull Player player) {
        TitleModule.getInstance().setPlayerPrefix(player, this);
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
    public @NotNull Section getSection() {
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
