package uk.firedev.firefly.modules.elevator;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.config.BasicConfig;
import uk.firedev.daisylib.messages.message.ComponentMessage;
import uk.firedev.daisylib.messages.replacer.Replacer;
import uk.firedev.daisylib.utils.CommonUtils;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;

import java.util.Map;

public class ElevatorConfig extends BasicConfig {

    private static ElevatorConfig instance;

    private ElevatorConfig() {
        super("modules/elevators.yml", "modules/elevators.yml", Firefly.getInstance());
    }

    public static ElevatorConfig getInstance() {
        if (instance == null) {
            instance = new ElevatorConfig();
        }
        return instance;
    }

    public ComponentMessage<?, ?>  getCommandUsageMessage() {
        return getComponentMessage("messages.command.usage", "<color:#F0E68C>Usage: <aqua>/elevator giveblock/unsetElevator");
    }

    public ComponentMessage<?, ?>  getCommandGivenMessage() {
        return getComponentMessage("messages.command.block-given", "<color:#F0E68C>Given you an Elevator Block!</color>");
    }

    public ComponentMessage<?, ?>  getCommandUnregisterMessage() {
        return getComponentMessage("messages.command.unregistered-elevator", "<color:#F0E68C>Successfully removed elevator data from this block.</color>");
    }

    public ComponentMessage<?, ?>  getCommandInvalidMessage() {
        return getComponentMessage("messages.command.not-an-elevator", "<red>This block is not an elevator!</red>");
    }

    public ComponentMessage<?, ?>  getUnsafeLocationMessage() {
        return getComponentMessage("messages.unsafe-location", "<red>The target elevator is unsafe!</red>");
    }

    public Component getBossBarTitle(@NonNull Elevator elevator) {
        Replacer replacer = Replacer.replacer().addReplacements(Map.of(
                "{current}", (elevator.getCurrentPosition() + 1),
                "{all}", elevator.getStack().size(),
                "{y}", elevator.getTPLocation().getY()
        ));
        return getComponentMessage("bossbar.title", "<yellow>Floor {current} of {all}</yellow>").replace(replacer).toSingleMessage().get();
    }

    public BossBar.Color getBossBarColor() {
        return CommonUtils.getEnumValue(
            BossBar.Color.class,
            getConfig().getString("bossbar.color"),
            BossBar.Color.RED
        );
    }

    public BossBar.Overlay getBossBarOverlay() {
        return CommonUtils.getEnumValue(
            BossBar.Overlay.class,
            getConfig().getString("bossbar.overlay"),
            BossBar.Overlay.PROGRESS
        );
    }

    public BossBar getBossBar(@NonNull Elevator elevator) {
        float progress;
        if (elevator.getCurrentPosition() == -1 || elevator.getStack().isEmpty()) {
            progress = 1F;
        } else {
            progress = (float) (elevator.getCurrentPosition() + 1) / elevator.getStack().size();
        }

        return BossBar.bossBar(
            getBossBarTitle(elevator),
            progress,
            getBossBarColor(),
            getBossBarOverlay()
        );
    }

    @Override
    public ComponentMessage<?, ?> getComponentMessage(@NonNull String path, @NonNull Object def) {
        return super.getComponentMessage(path, def).replace("{prefix}", MessageConfig.getInstance().getPrefix());
    }
    
}
