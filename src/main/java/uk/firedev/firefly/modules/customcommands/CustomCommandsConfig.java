package uk.firedev.firefly.modules.customcommands;

import uk.firedev.daisylib.Config;
import uk.firedev.daisylib.libs.boostedyaml.block.implementation.Section;
import uk.firedev.firefly.Firefly;

import java.util.ArrayList;
import java.util.List;

public class CustomCommandsConfig extends Config {

    private static CustomCommandsConfig instance;

    private CustomCommandsConfig() {
        super("modules/customcommands.yml", "modules/customcommands.yml", Firefly.getInstance(), false);
    }

    public static CustomCommandsConfig getInstance() {
        if (instance == null) {
            instance = new CustomCommandsConfig();
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

    public List<String> getCommandBuilderNames() {
        List<String> names = new ArrayList<>();
        for (CommandBuilder builder : getCommandBuilders()) {
            names.add(builder.getCommandName());
            names.addAll(builder.getAliases());
        }
        return names;
    }

}
