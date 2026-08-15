package uk.firedev.firefly.modules.kit;

import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.config.BasicConfig;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.daisylib.messages.message.ComponentMessage;

import java.util.List;
import java.util.Objects;

public class KitConfig extends BasicConfig {

    private static KitConfig instance;

    private KitConfig() {
        super("modules/kits.yml", "modules/kits.yml", Firefly.getInstance());
    }

    public static KitConfig getInstance() {
        if (instance == null) {
            instance = new KitConfig();
        }
        return instance;
    }

    public ComponentMessage<?, ?>  getAwardedCommandMessage() {
        return getComponentMessage("messages.command.awarded-command", "{prefix}<color:#F0E68C>Given {player} the kit {kit}.</color>");
    }

    public ComponentMessage<?, ?>  getAwardedReceiverMessage() {
        return getComponentMessage("messages.command.awarded-receive", "{prefix}<color:#F0E68C>You have been given the kit {kit}.</color>");
    }

    public ComponentMessage<?, ?>  getOnCooldownMessage() {
        return getComponentMessage("messages.gui.on-cooldown", "{prefix}<red>This kit is on cooldown! You can obtain it again in <yellow>{timeLeft}<red>!");
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

    @Override
    public ComponentMessage<?, ?> getComponentMessage(@NonNull String path, @NonNull Object def) {
        return super.getComponentMessage(path, def).replace("{prefix}", MessageConfig.getInstance().getPrefix());
    }

}
