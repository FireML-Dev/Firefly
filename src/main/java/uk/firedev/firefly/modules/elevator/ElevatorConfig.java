package uk.firedev.firefly.modules.elevator;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import uk.firedev.daisylib.api.builders.BossBarBuilder;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.api.message.component.ComponentReplacer;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;

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
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "messages.command.usage", "<color:#F0E68C>Usage: <aqua>/elevator giveblock/unsetElevator");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getCommandGivenMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "messages.command.block-given", "<color:#F0E68C>Given you an Elevator Block!</color>");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getCommandUnregisterMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "messages.command.unregistered-elevator", "<color:#F0E68C>Successfully removed elevator data from this block.</color>");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getCommandInvalidMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "messages.command.not-an-elevator", "<red>This block is not an elevator!</red>");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getUnsafeLocationMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "messages.unsafe-location", "<red>The target elevator is unsafe!</red>");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public Component getBossBarTitle(@NotNull Elevator elevator) {
        ComponentReplacer replacer = ComponentReplacer.create(
                "current", String.valueOf(elevator.getCurrentPosition() + 1),
                "all", String.valueOf(elevator.getStack().size()),
                "y", String.valueOf((int) elevator.getTPLocation().getY())
        );
        return ComponentMessage.fromConfig(getConfig(), "bossbar.title", "<yellow>Floor {current} of {all}</yellow>").applyReplacer(replacer).getMessage();
    }

    public BossBar.Color getBossBarColor() {
        try {
            return BossBar.Color.valueOf(getConfig().getString("bossbar.color", "RED").toUpperCase());
        } catch (IllegalArgumentException ex) {
            return BossBar.Color.RED;
        }
    }

    public BossBar.Overlay getBossBarOverlay() {
        try {
            return BossBar.Overlay.valueOf(getConfig().getString("bossbar.overlay", "PROGRESS").toUpperCase());
        } catch (IllegalArgumentException ex) {
            return BossBar.Overlay.PROGRESS;
        }
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
