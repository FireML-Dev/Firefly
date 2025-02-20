package uk.firedev.firefly.modules.kit;

import org.bukkit.configuration.ConfigurationSection;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;

import java.util.List;
import java.util.Objects;

public class KitConfig extends ConfigBase {

    private static KitConfig instance;

    private KitConfig() {
        super("modules/kits.yml", "modules/kits.yml", Firefly.getInstance());
        withDefaultUpdaterSettings();
    }

    public static KitConfig getInstance() {
        if (instance == null) {
            instance = new KitConfig();
        }
        return instance;
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

    public ComponentMessage getOnCooldownMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "messages.gui.on-cooldown", "{prefix}<red>This kit is on cooldown! You can obtain it again in <yellow>{timeLeft}<red>!");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public List<ConfigurationSection> getKitConfigs() {
        ConfigurationSection kitsSection = getConfig().getConfigurationSection("kits");
        if (kitsSection == null) {
            return List.of();
        }
        return kitsSection.getKeys(false).stream()
                .map(kitsSection::getConfigurationSection)
                .filter(Objects::nonNull)
                .toList();
    }

}
