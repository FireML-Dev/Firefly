package uk.firedev.skylight.modules.alias;

import org.bukkit.configuration.ConfigurationSection;
import uk.firedev.daisylib.Config;
import uk.firedev.skylight.Skylight;

import java.util.ArrayList;
import java.util.List;

public class AliasConfig extends Config {

    private static AliasConfig instance;

    private AliasConfig() {
        super("aliases.yml", Skylight.getInstance(), false, false);
    }

    public static AliasConfig getInstance() {
        if (instance == null) {
            instance = new AliasConfig();
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
            String name = section.getName();
            List<String> aliases = section.getStringList("aliases");
            String permission = section.getString("permission");
            List<String> commands = section.getStringList("commands");
            List<String> messages = section.getStringList("messages");
            list.add(new CommandBuilder(name, aliases, permission, commands, messages));
        });
        return list;
    }

}
