package uk.firedev.skylight.modules.titles;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import uk.firedev.daisylib.Config;
import uk.firedev.daisylib.Loggers;
import uk.firedev.daisylib.message.component.ComponentMessage;
import uk.firedev.skylight.Skylight;
import uk.firedev.skylight.config.MessageConfig;
import uk.firedev.skylight.modules.titles.objects.Prefix;
import uk.firedev.skylight.modules.titles.objects.Suffix;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TitleConfig extends Config {

    private static TitleConfig instance;

    private TitleConfig() {
        super("titles.yml", Skylight.getInstance(), true, false);
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
                Loggers.logException(ex, Skylight.getInstance().getLogger());
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
                Loggers.logException(ex, Skylight.getInstance().getLogger());
            }
        });
        return suffixes;
    }

    // TITLE MESSAGES

    public ComponentMessage getPrefixSetMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.prefix-set", "<color:#F0E68C>Applied Prefix {prefix}.</color>");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getPrefixRemovedMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.prefix-removed", "<red>Removed Current Prefix.</red>");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getSuffixSetMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.suffix-set", "<color:#F0E68C>Applied Suffix {suffix}.</color>");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

    public ComponentMessage getSuffixRemovedMessage() {
        ComponentMessage message = new ComponentMessage(getConfig(), "messages.suffix-removed", "<red>Removed Current Suffix.</red>");
        message = message.applyReplacer(MessageConfig.getInstance().getPrefixReplacer());
        return message;
    }

}
