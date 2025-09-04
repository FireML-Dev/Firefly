package uk.firedev.firefly.modules.elevator;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Boss;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.builders.BossBarBuilder;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.daisylib.utils.ObjectUtils;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.messagelib.message.ComponentMessage;
import uk.firedev.messagelib.replacer.Replacer;

import java.util.Map;

public class ElevatorConfig extends ConfigBase {

    private static ElevatorConfig instance;

    private ElevatorConfig() {
        super("modules/elevators.yml", "modules/elevators.yml", Firefly.getInstance());
        withDefaultUpdaterSettings();
    }

    public static ElevatorConfig getInstance() {
        if (instance == null) {
            instance = new ElevatorConfig();
        }
        return instance;
    }

    public ComponentMessage getCommandUsageMessage() {
        return getComponentMessage("messages.command.usage", "<color:#F0E68C>Usage: <aqua>/elevator giveblock/unsetElevator").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getCommandGivenMessage() {
        return getComponentMessage("messages.command.block-given", "<color:#F0E68C>Given you an Elevator Block!</color>").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getCommandUnregisterMessage() {
        return getComponentMessage("messages.command.unregistered-elevator", "<color:#F0E68C>Successfully removed elevator data from this block.</color>").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getCommandInvalidMessage() {
        return getComponentMessage("messages.command.not-an-elevator", "<red>This block is not an elevator!</red>").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public ComponentMessage getUnsafeLocationMessage() {
        return getComponentMessage("messages.unsafe-location", "<red>The target elevator is unsafe!</red>").replace(MessageConfig.getInstance().getPrefixReplacer());
    }

    public Component getBossBarTitle(@NotNull Elevator elevator) {
        Replacer replacer = Replacer.replacer().addReplacements(Map.of(
                "current", (elevator.getCurrentPosition() + 1),
                "all", elevator.getStack().size(),
                "y", elevator.getTPLocation().getY()
        ));
        return getComponentMessage("bossbar.title", "<yellow>Floor {current} of {all}</yellow>").replace(replacer).toSingleMessage().get();
    }

    public BossBar.Color getBossBarColor() {
        return ObjectUtils.getEnumValue(
            BossBar.Color.class,
            getConfig().getString("bossbar.color"),
            BossBar.Color.RED
        );
    }

    public BossBar.Overlay getBossBarOverlay() {
        return ObjectUtils.getEnumValue(
            BossBar.Overlay.class,
            getConfig().getString("bossbar.overlay"),
            BossBar.Overlay.PROGRESS
        );
    }

    public BossBar getBossBar(@NotNull Elevator elevator) {
        float progress;
        if (elevator.getCurrentPosition() == -1 || elevator.getStack().isEmpty()) {
            progress = 1F;
        } else {
            progress = (float) (elevator.getCurrentPosition() + 1) / elevator.getStack().size();
        }

        return BossBarBuilder.create()
                .withTitle(getBossBarTitle(elevator), null)
                .withColor(getBossBarColor())
                .withOverlay(getBossBarOverlay())
                .withProgress(progress)
                .build();
    }

}
