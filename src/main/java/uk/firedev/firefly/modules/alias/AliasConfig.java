package uk.firedev.firefly.modules.alias;

import uk.firedev.daisylib.Config;
import uk.firedev.daisylib.libs.boostedyaml.block.implementation.Section;
import uk.firedev.firefly.Firefly;

import java.util.ArrayList;
import java.util.List;

public class AliasConfig extends Config {

    private static AliasConfig instance;

    private AliasConfig() {
        super("aliases.yml", "aliases.yml", Firefly.getInstance(), false);
    }

    public static AliasConfig getInstance() {
        if (instance == null) {
            instance = new AliasConfig();
        }
        return instance;
    }

    public List<CommandBuilder> getCommandBuilders() {
        List<CommandBuilder> list = new ArrayList<>();
        getConfig().getRoutesAsStrings(false).forEach(key -> {
            Section section = getConfig().getSection(key);
            if (section == null) {
                return;
            }
            String name = section.getNameAsString();
            List<String> aliases = section.getStringList("aliases");
            String permission = section.getString("permission");
            List<String> commands = section.getStringList("commands");
            List<String> messages = section.getStringList("messages");
            list.add(new CommandBuilder(name, aliases, permission, commands, messages));
        });
        return list;
    }

}
