package uk.firedev.firefly.modules.titles;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import uk.firedev.daisylib.api.Loggers;
import uk.firedev.daisylib.api.message.component.ComponentMessage;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.titles.objects.Prefix;
import uk.firedev.firefly.modules.titles.objects.Suffix;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TitleConfig extends ConfigBase {

    private static TitleConfig instance;

    private TitleConfig() {
        super("modules/titles.yml", "modules/titles.yml", Firefly.getInstance());
        withDefaultUpdaterSettings();
    }

    public static TitleConfig getInstance() {
        if (instance == null) {
            instance = new TitleConfig();
        }
        return instance;
    }

    public List<Prefix> getPrefixesFromFile() {
        ConfigurationSection section = getConfig().getConfigurationSection("prefixes");
        if (section == null) {
            return List.of();
        }
        List<Prefix> prefixes = new ArrayList<>();
        section.getKeys(false).stream().map(section::getConfigurationSection).filter(Objects::nonNull).forEach(prefixSection -> {
            try {
                prefixes.add(new Prefix(prefixSection));
            } catch (InvalidConfigurationException ex) {
                Loggers.logException(Firefly.getInstance().getComponentLogger(), ex);
            }
        });
        return prefixes;
    }

    public List<Suffix> getSuffixesFromFile() {
        ConfigurationSection section = getConfig().getConfigurationSection("suffixes");
        if (section == null) {
            return List.of();
        }
        List<Suffix> suffixes = new ArrayList<>();
        section.getKeys(false).stream().map(section::getConfigurationSection).filter(Objects::nonNull).forEach(prefixSection -> {
            try {
                suffixes.add(new Suffix(prefixSection));
            } catch (InvalidConfigurationException ex) {
                Loggers.logException(Firefly.getInstance().getComponentLogger(), ex);
            }
        });
        return suffixes;
    }

    // TITLE MESSAGES

    public ComponentMessage getPrefixSetMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "messages.prefix-set", "<color:#F0E68C>Applied Prefix {prefix}.</color>");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getPrefixRemovedMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "messages.prefix-removed", "<red>Removed Current Prefix.</red>");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getPrefixDisplayMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "messages.prefix-display", "<color:#F0E68C>Current Prefix: <white>{prefix}");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getSuffixSetMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "messages.suffix-set", "<color:#F0E68C>Applied Suffix {suffix}.</color>");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getSuffixRemovedMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "messages.suffix-removed", "<red>Removed Current Suffix.</red>");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getSuffixDisplayMessage() {
        ComponentMessage message = ComponentMessage.fromConfig(getConfig(), "messages.suffix-display", "<color:#F0E68C>Current Suffix: <white>{suffix}");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

}
