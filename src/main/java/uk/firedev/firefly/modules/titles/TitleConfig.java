package uk.firedev.firefly.modules.titles;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.config.BasicConfig;
import uk.firedev.daisylib.messages.message.ComponentMessage;
import uk.firedev.firefly.Firefly;
import uk.firedev.firefly.config.MessageConfig;
import uk.firedev.firefly.modules.titles.objects.Prefix;
import uk.firedev.firefly.modules.titles.objects.Suffix;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TitleConfig extends BasicConfig {

    private static TitleConfig instance;

    private TitleConfig() {
        super("modules/titles.yml", "modules/titles.yml", Firefly.getInstance());
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
                Firefly.getInstance().getLogging().exception(ex);
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
                Firefly.getInstance().getLogging().exception(ex);
            }
        });
        return suffixes;
    }

    // TITLE MESSAGES

    public ComponentMessage<?, ?>  getPrefixSetMessage() {
        return getComponentMessage("messages.prefix-set", "<color:#F0E68C>Applied Prefix {prefix}.</color>");
    }

    public ComponentMessage<?, ?>  getPrefixRemovedMessage() {
        return getComponentMessage("messages.prefix-removed", "<red>Removed Current Prefix.</red>");
    }

    public ComponentMessage<?, ?>  getPrefixDisplayMessage() {
        return getComponentMessage("messages.prefix-display", "<color:#F0E68C>Current Prefix: <white>{prefix}");
    }

    public ComponentMessage<?, ?>  getSuffixSetMessage() {
        return getComponentMessage("messages.suffix-set", "<color:#F0E68C>Applied Suffix {suffix}.</color>");
    }

    public ComponentMessage<?, ?>  getSuffixRemovedMessage() {
        return getComponentMessage("messages.suffix-removed", "<red>Removed Current Suffix.</red>");
    }

    public ComponentMessage<?, ?>  getSuffixDisplayMessage() {
        return getComponentMessage("messages.suffix-display", "<color:#F0E68C>Current Suffix: <white>{suffix}");
    }
    
    @Override
    public ComponentMessage<?, ?> getComponentMessage(@NonNull String path, @NonNull Object def) {
        return super.getComponentMessage(path, def).replace("{prefix}", MessageConfig.getInstance().getPrefix());
    }

}
