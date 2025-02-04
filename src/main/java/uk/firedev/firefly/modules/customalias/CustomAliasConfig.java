package uk.firedev.firefly.modules.customalias;

import org.bukkit.configuration.ConfigurationSection;
import uk.firedev.daisylib.config.ConfigBase;
import uk.firedev.firefly.Firefly;

import java.util.ArrayList;
import java.util.List;

public class CustomAliasConfig extends ConfigBase {

    private static CustomAliasConfig instance;

    private CustomAliasConfig() {
        super("modules/customalias.yml", "modules/customalias.yml", Firefly.getInstance());
    }

    public static CustomAliasConfig getInstance() {
        if (instance == null) {
            instance = new CustomAliasConfig();
        }
        return instance;
    }

    public List<CommandBuilder> getCommandBuilders() {
        List<CommandBuilder> list = new ArrayList<>();
        getConfig().getKeys(false).forEach(key -> {
            ConfigurationSection section = getConfig().getConfigurationSection(key);
            if (section == null) {
                return;
            }
            list.add(new CommandBuilder(section));
        });
        return list;
    }

    public List<String> getCommandBuilderNames() {
        List<String> names = new ArrayList<>();
        for (CommandBuilder builder : getCommandBuilders()) {
            names.add(builder.getCommandName());
            names.addAll(builder.getAliases());
        }
        return names;
    }

}
