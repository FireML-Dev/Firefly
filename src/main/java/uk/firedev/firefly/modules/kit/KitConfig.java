package uk.firedev.firefly.modules.kit;

import uk.firedev.daisylib.Config;
import uk.firedev.daisylib.libs.boostedyaml.block.implementation.Section;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;

import java.util.List;
import java.util.Objects;

public class KitConfig extends Config {

    private static KitConfig instance;

    // Does not remove unused config options, as that would wipe custom kits.
    private KitConfig() {
        super("modules/kits.yml", "modules/kits.yml", Firefly.getInstance(), true);
    }

    public static KitConfig getInstance() {
        if (instance == null) {
            instance = new KitConfig();
        }
        return instance;
    }

    public ComponentMessage getNotFoundMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "messages.command.kit-not-found", "{prefix}<red>Kit not found.");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getAwardedCommandMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "messages.command.awarded-command", "{prefix}<color:#F0E68C>Given {player} the kit {kit}.</color>");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getAwardedReceiverMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "messages.command.awarded-receive", "{prefix}<color:#F0E68C>You have been given the kit {kit}.</color>");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getGUIOnCooldownMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "messages.gui.on-cooldown", "{prefix}<red>This kit is on cooldown! You can obtain it again in <yellow>{timeLeft}<red>!");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public List<Section> getKitConfigs() {
        Section kitsSection = getConfig().getSection("kits");
        if (kitsSection == null) {
            return List.of();
        }
        return kitsSection.getRoutesAsStrings(false).stream()
                .map(kitsSection::getSection)
                .filter(Objects::nonNull)
                .toList();
    }

}
